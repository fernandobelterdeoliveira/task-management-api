package com.example.taskmanagement.task.controller;

import com.example.taskmanagement.task.dto.TaskResponse;
import com.example.taskmanagement.task.exception.TaskNotFoundException;
import com.example.taskmanagement.task.model.TaskPriority;
import com.example.taskmanagement.task.model.TaskStatus;
import com.example.taskmanagement.task.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void getTasksReturnsTasks() throws Exception {
        Mockito.when(taskService.getTasks(null, null, null))
                .thenReturn(List.of(taskResponse(1L, "Read task", TaskStatus.TODO, TaskPriority.MEDIUM)));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Read task"));
    }

    @Test
    void getTasksPassesOptionalFiltersToService() throws Exception {
        Mockito.when(taskService.getTasks(TaskStatus.TODO, TaskPriority.HIGH, "api"))
                .thenReturn(List.of(taskResponse(1L, "API task", TaskStatus.TODO, TaskPriority.HIGH)));

        mockMvc.perform(get("/api/tasks")
                        .param("status", "TODO")
                        .param("priority", "HIGH")
                        .param("search", "api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("TODO"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"));
    }

    @Test
    void getTaskByIdReturnsTask() throws Exception {
        Mockito.when(taskService.getTaskById(1L))
                .thenReturn(taskResponse(1L, "Read task", TaskStatus.TODO, TaskPriority.MEDIUM));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Read task"));
    }

    @Test
    void getTaskByIdReturnsNotFoundWhenMissing() throws Exception {
        Mockito.when(taskService.getTaskById(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Task not found with id 99"));
    }

    @Test
    void createTaskReturnsCreatedTask() throws Exception {
        Mockito.when(taskService.createTask(any()))
                .thenReturn(taskResponse(1L, "Create task", TaskStatus.TODO, TaskPriority.HIGH));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Create task",
                                  "description": "Create a task from the API",
                                  "priority": "HIGH",
                                  "dueDate": "2026-06-01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Create task"));
    }

    @Test
    void createTaskReturnsValidationErrorForMissingTitle() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Missing title"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.title").value("Title is required"));
    }

    @Test
    void updateTaskReturnsUpdatedTask() throws Exception {
        Mockito.when(taskService.updateTask(eq(1L), any()))
                .thenReturn(taskResponse(1L, "Updated task", TaskStatus.IN_PROGRESS, TaskPriority.HIGH));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Updated task",
                                  "description": "Updated from test",
                                  "status": "IN_PROGRESS",
                                  "priority": "HIGH",
                                  "dueDate": "2026-06-05"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated task"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateTaskStatusReturnsUpdatedStatus() throws Exception {
        Mockito.when(taskService.updateTaskStatus(1L, TaskStatus.DONE))
                .thenReturn(taskResponse(1L, "Finish task", TaskStatus.DONE, TaskPriority.MEDIUM));

        mockMvc.perform(patch("/api/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "DONE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void deleteTaskReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).deleteTask(1L);
    }

    private TaskResponse taskResponse(Long id, String title, TaskStatus status, TaskPriority priority) {
        TaskResponse response = new TaskResponse();
        response.setId(id);
        response.setTitle(title);
        response.setDescription("Description for " + title);
        response.setStatus(status);
        response.setPriority(priority);
        response.setDueDate(LocalDate.of(2026, 6, 1));
        response.setCreatedAt(LocalDateTime.of(2026, 5, 26, 10, 0));
        response.setUpdatedAt(LocalDateTime.of(2026, 5, 26, 10, 0));
        return response;
    }
}

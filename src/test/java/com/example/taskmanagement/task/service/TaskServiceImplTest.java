package com.example.taskmanagement.task.service;

import com.example.taskmanagement.task.dto.TaskCreateRequest;
import com.example.taskmanagement.task.dto.TaskResponse;
import com.example.taskmanagement.task.dto.TaskUpdateRequest;
import com.example.taskmanagement.task.exception.TaskNotFoundException;
import com.example.taskmanagement.task.mapper.TaskMapper;
import com.example.taskmanagement.task.model.Task;
import com.example.taskmanagement.task.model.TaskPriority;
import com.example.taskmanagement.task.model.TaskStatus;
import com.example.taskmanagement.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository, new TaskMapper());
    }

    @Test
    void createTaskSavesNewTaskWithDefaults() {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Write tests");
        request.setDescription("Add service tests");
        request.setDueDate(LocalDate.of(2026, 6, 1));

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(1L);
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            return task;
        });

        TaskResponse response = taskService.createTask(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Write tests");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(response.getPriority()).isEqualTo(TaskPriority.MEDIUM);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getTitle()).isEqualTo("Write tests");
    }

    @Test
    void getTaskByIdReturnsTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task(1L, "Read task", TaskStatus.TODO, TaskPriority.LOW)));

        TaskResponse response = taskService.getTaskById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Read task");
    }

    @Test
    void getTaskByIdThrowsWhenTaskDoesNotExist() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with id 99");
    }

    @Test
    void updateTaskChangesEditableFields() {
        Task existingTask = task(1L, "Old title", TaskStatus.TODO, TaskPriority.LOW);
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("New title");
        request.setDescription("New description");
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDate.of(2026, 6, 10));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskResponse response = taskService.updateTask(1L, request);

        assertThat(response.getTitle()).isEqualTo("New title");
        assertThat(response.getDescription()).isEqualTo("New description");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(response.getPriority()).isEqualTo(TaskPriority.HIGH);
    }

    @Test
    void updateTaskStatusChangesOnlyStatus() {
        Task existingTask = task(1L, "Status task", TaskStatus.TODO, TaskPriority.MEDIUM);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskResponse response = taskService.updateTaskStatus(1L, TaskStatus.DONE);

        assertThat(response.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(response.getTitle()).isEqualTo("Status task");
    }

    @Test
    void deleteTaskRemovesExistingTask() {
        Task existingTask = task(1L, "Delete task", TaskStatus.TODO, TaskPriority.MEDIUM);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(existingTask);
    }

    @Test
    void getTasksUsesRepositorySpecificationForFilters() {
        when(taskRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(task(1L, "Important task", TaskStatus.TODO, TaskPriority.HIGH)));

        List<TaskResponse> responses = taskService.getTasks(TaskStatus.TODO, TaskPriority.HIGH, "important");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(responses.get(0).getPriority()).isEqualTo(TaskPriority.HIGH);
        verify(taskRepository).findAll(any(Specification.class));
    }

    private Task task(Long id, String title, TaskStatus status, TaskPriority priority) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("Description for " + title);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(LocalDate.of(2026, 6, 1));
        task.setCreatedAt(LocalDateTime.of(2026, 5, 26, 10, 0));
        task.setUpdatedAt(LocalDateTime.of(2026, 5, 26, 10, 0));
        return task;
    }
}

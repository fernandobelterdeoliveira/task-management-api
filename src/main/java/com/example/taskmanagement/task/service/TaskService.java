package com.example.taskmanagement.task.service;

import com.example.taskmanagement.task.dto.TaskCreateRequest;
import com.example.taskmanagement.task.dto.TaskResponse;
import com.example.taskmanagement.task.dto.TaskUpdateRequest;
import com.example.taskmanagement.task.model.TaskPriority;
import com.example.taskmanagement.task.model.TaskStatus;

import java.util.List;

public interface TaskService {

    List<TaskResponse> getTasks(TaskStatus status, TaskPriority priority, String search);

    TaskResponse getTaskById(Long id);

    TaskResponse createTask(TaskCreateRequest request);

    TaskResponse updateTask(Long id, TaskUpdateRequest request);

    TaskResponse updateTaskStatus(Long id, TaskStatus status);

    void deleteTask(Long id);
}

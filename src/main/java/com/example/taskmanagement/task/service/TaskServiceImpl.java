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
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(TaskStatus status, TaskPriority priority, String search) {
        Specification<Task> specification = buildTaskSpecification(status, priority, search);

        return taskRepository.findAll(specification)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        return taskMapper.toResponse(findTaskById(id));
    }

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        Task task = taskMapper.toEntity(request);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponse(savedTask);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        Task task = findTaskById(id);
        taskMapper.updateEntity(task, request);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponse(savedTask);
    }

    @Override
    public TaskResponse updateTaskStatus(Long id, TaskStatus status) {
        Task task = findTaskById(id);
        task.setStatus(status);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponse(savedTask);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    private Specification<Task> buildTaskSpecification(TaskStatus status, TaskPriority priority, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }

            if (search != null && !search.isBlank()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate titleMatches = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern);
                Predicate descriptionMatches = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern);
                predicates.add(criteriaBuilder.or(titleMatches, descriptionMatches));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

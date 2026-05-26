package com.example.taskmanagement.config;

import com.example.taskmanagement.task.model.Task;
import com.example.taskmanagement.task.model.TaskPriority;
import com.example.taskmanagement.task.model.TaskStatus;
import com.example.taskmanagement.task.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TaskRepository taskRepository;

    public DataSeeder(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        if (taskRepository.count() > 0) {
            return;
        }

        taskRepository.save(createTask(
                "Create project structure",
                "Set up the Spring Boot packages, Maven dependencies, and application configuration.",
                TaskStatus.DONE,
                TaskPriority.HIGH,
                LocalDate.now().plusDays(1)));

        taskRepository.save(createTask(
                "Implement REST endpoints",
                "Build CRUD endpoints for creating, reading, updating, and deleting tasks.",
                TaskStatus.IN_PROGRESS,
                TaskPriority.HIGH,
                LocalDate.now().plusDays(3)));

        taskRepository.save(createTask(
                "Write README examples",
                "Document how to run the API and test it with curl.",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                LocalDate.now().plusDays(5)));
    }

    private Task createTask(
            String title,
            String description,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueDate) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        return task;
    }
}

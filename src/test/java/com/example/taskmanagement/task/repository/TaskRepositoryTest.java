package com.example.taskmanagement.task.repository;

import com.example.taskmanagement.task.model.Task;
import com.example.taskmanagement.task.model.TaskPriority;
import com.example.taskmanagement.task.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void saveAndFindByIdPersistsTask() {
        Task savedTask = taskRepository.save(task("Persist task", TaskStatus.TODO, TaskPriority.MEDIUM));

        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());

        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Persist task");
        assertThat(foundTask.get().getCreatedAt()).isNotNull();
        assertThat(foundTask.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void deleteRemovesTask() {
        Task savedTask = taskRepository.save(task("Delete task", TaskStatus.TODO, TaskPriority.LOW));

        taskRepository.delete(savedTask);

        assertThat(taskRepository.findById(savedTask.getId())).isEmpty();
    }

    @Test
    void findAllWithStatusSpecificationFiltersByStatus() {
        taskRepository.save(task("Todo task", TaskStatus.TODO, TaskPriority.MEDIUM));
        taskRepository.save(task("Done task", TaskStatus.DONE, TaskPriority.MEDIUM));

        Specification<Task> statusIsTodo = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), TaskStatus.TODO);

        List<Task> tasks = taskRepository.findAll(statusIsTodo);

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Todo task");
        assertThat(tasks.get(0).getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    void findAllWithPrioritySpecificationFiltersByPriority() {
        taskRepository.save(task("Low priority task", TaskStatus.TODO, TaskPriority.LOW));
        taskRepository.save(task("High priority task", TaskStatus.TODO, TaskPriority.HIGH));

        Specification<Task> priorityIsHigh = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("priority"), TaskPriority.HIGH);

        List<Task> tasks = taskRepository.findAll(priorityIsHigh);

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("High priority task");
        assertThat(tasks.get(0).getPriority()).isEqualTo(TaskPriority.HIGH);
    }

    private Task task(String title, TaskStatus status, TaskPriority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("Description for " + title);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(LocalDate.of(2026, 6, 1));
        return task;
    }
}

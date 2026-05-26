# Presentation Script: Spring Boot To-Do API

## Slide 1: Introduction

Hello everyone. In this presentation, I will explain a beginner-friendly To-Do REST API built with Java and Spring Boot.

The goal of this project is to manage tasks. A user can create tasks, list tasks, update tasks, change a task status, delete tasks, and filter tasks.

This project is useful because it shows the basic structure of a real backend API without adding unnecessary complexity.

## Slide 2: What The Project Does

This API manages To-Do tasks.

Each task has a title, description, status, priority, due date, creation date, and update date.

The available task statuses are TODO, IN_PROGRESS, and DONE.

The available priorities are LOW, MEDIUM, and HIGH.

The API supports the main CRUD operations:

```text
Create
Read
Update
Delete
```

It also supports filtering by status, filtering by priority, and searching by text.

## Slide 3: Technologies Used

The project uses Java 17 as the programming language.

It uses Spring Boot to start and configure the application.

It uses Spring Web to create REST endpoints.

It uses Spring Data JPA to communicate with the database.

It uses Spring Validation to validate request data.

It uses H2 as a simple in-memory database.

It uses Maven to manage dependencies and run the application.

For testing, it uses JUnit 5, Mockito, MockMvc, and Spring Boot test tools.

## Slide 4: Folder Structure

The main package is:

```text
com.example.taskmanagement
```

Inside this package, the code is organized by responsibility.

The `task` package contains the main feature of the application.

Inside `task`, we have controller, service, repository, model, DTO, mapper, and exception packages.

The `common` package contains shared error handling.

The `config` package contains startup configuration, including sample data.

This structure makes it easy to find code because each folder has a clear purpose.

## Slide 5: Controller Layer

The controller is responsible for HTTP.

In this project, the controller file is:

```text
TaskController.java
```

It exposes endpoints like:

```text
GET /api/tasks
POST /api/tasks
PUT /api/tasks/{id}
DELETE /api/tasks/{id}
```

The controller receives requests, validates input, calls the service, and returns responses.

It does not talk directly to the database.

This keeps the controller simple and focused.

## Slide 6: Service Layer

The service layer contains the application logic.

The service files are:

```text
TaskService.java
TaskServiceImpl.java
```

The interface defines what the service can do.

The implementation contains the actual logic.

For example, when we ask for a task by id, the service checks whether the task exists.

If the task does not exist, it throws a TaskNotFoundException.

This is better than putting that logic inside the controller.

## Slide 7: Repository Layer

The repository layer handles database access.

The repository file is:

```text
TaskRepository.java
```

It extends Spring Data JPA interfaces.

Because of this, Spring automatically gives us methods like save, findById, findAll, delete, and count.

This means we do not need to write basic SQL for common operations.

The repository also supports specifications, which are used for filtering tasks by status, priority, and search text.

## Slide 8: Entity

The entity represents the database table.

The entity file is:

```text
Task.java
```

It uses JPA annotations such as:

```text
@Entity
@Table
@Id
@GeneratedValue
```

These annotations tell Spring Data JPA how to store the object in the database.

The Task entity is internal to the application. We do not expose it directly through the API.

## Slide 9: DTOs

DTO means Data Transfer Object.

DTOs define what the API receives and returns.

This project has DTOs for creating a task, updating a task, changing only the status, and returning a task response.

Using DTOs is important because the API contract should be separate from the database entity.

It also lets us add validation rules such as required title and maximum text length.

For example, if a user tries to create a task without a title, the API returns a validation error.

## Slide 10: Mapper

The mapper converts between DTOs and entities.

The mapper file is:

```text
TaskMapper.java
```

For example, when the user sends a create request, the mapper creates a Task entity.

When the API returns data, the mapper converts a Task entity into a TaskResponse DTO.

This keeps conversion code in one place.

## Slide 11: Error Handling

The project has consistent error handling.

The main files are:

```text
GlobalExceptionHandler.java
ApiError.java
TaskNotFoundException.java
```

If a task is not found, the API returns a 404 response.

If validation fails, the API returns a 400 response with the invalid fields.

This makes the API easier to use because errors always follow the same structure.

## Slide 12: H2 Database

H2 is a lightweight database that runs inside the application.

This project uses an in-memory H2 database.

That means the data exists while the app is running and disappears when the app stops.

This is useful for learning because we do not need to install MySQL, PostgreSQL, or another database.

The H2 console is available in the browser at:

```text
http://localhost:8080/h2-console
```

The JDBC URL is:

```text
jdbc:h2:mem:taskdb
```

## Slide 13: Running The Project

To run the project, we need Java 17 and Maven installed.

From the project folder, run:

```bash
mvn spring-boot:run
```

The application starts at:

```text
http://localhost:8080
```

To run the tests, use:

```bash
mvn test
```

## Slide 14: Testing With Postman

In Postman, we use this base URL:

```text
http://localhost:8080
```

To list tasks, send a GET request to:

```text
/api/tasks
```

To create a task, send a POST request to:

```text
/api/tasks
```

Use JSON in the request body:

```json
{
  "title": "Learn Spring Boot",
  "description": "Build a REST API",
  "priority": "HIGH",
  "dueDate": "2026-06-01"
}
```

To update a task, use PUT.

To change only the status, use PATCH.

To delete a task, use DELETE.

## Slide 15: Why The Architecture Is Clean

This architecture is clean because every layer has one main responsibility.

The controller handles HTTP.

The service handles business logic.

The repository handles database access.

The entity represents the database model.

The DTOs represent the API input and output.

The mapper converts between DTOs and entities.

The exception handler keeps errors consistent.

This separation makes the project easier to read, test, debug, and extend.

## Slide 16: Conclusion

This project is a good introduction to backend development with Spring Boot.

It covers REST endpoints, validation, database access, clean layering, error handling, seed data, and testing.

It is simple enough for beginners, but it follows patterns that are also used in real-world applications.

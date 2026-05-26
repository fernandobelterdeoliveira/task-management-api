# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project follows semantic versioning.

## [1.0.0] - 2026-05-26

### Added

- Initial Spring Boot project using Java 17 and Maven.
- Task CRUD endpoints under `/api/tasks`.
- Endpoint for changing only task status with `PATCH /api/tasks/{id}/status`.
- Optional task filters for `status`, `priority`, and `search`.
- H2 in-memory database configuration.
- Spring Data JPA persistence with a `Task` entity.
- DTOs for create, update, status update, and response payloads.
- Request validation with Spring Validation annotations.
- Global exception handling with consistent JSON error responses.
- Custom `TaskNotFoundException` for missing task records.
- Seed data loaded at application startup.
- Beginner-friendly README documentation with endpoint examples and project explanation.
- Service, controller, and repository test classes.

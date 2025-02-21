# Changelog

## [v1.0.0] - 2025-02-19
### Added
- Creating User, Role objects for user identification and authorization and storing them in the DB.
- Creating services and controllers for processing user requests (`sign in`, `create`, `change password`, `block`, `unblock`, `delete`).
- Connecting Keycloak for administering user accounts and roles.
- Creating custom error types and a common error handler for controllers.

## Git Flow History
1. Initial Git Flow for project `git flow init`
2. The `feature/add-user-crud-v1.0.0` branch was completed and merged into `develop`.
3. The `feature/add-application-files-v1.0.0` branch was completed and merged into `develop`.

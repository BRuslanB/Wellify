# Changelog

## [v1.0.0] - 2025-02-19
### Added
- Creating User, Role objects for user identification and authorization and storing them in the DB.
- Creating services and controllers for processing user requests (`sign in`, `create`, `change password`, `block`, `unblock`, `delete`).
- Connecting Keycloak for administering user accounts and roles.
- Creating custom error types and a common error handler for controllers.

## [v1.1.0] - 2025-02-27
### Added
- Separation of authorization and user management endpoints into 2 separate controllers.
- Creation of AOP aspects for centralized logging of service method execution, errors, and transactions.
- Checking tokens of deleted and blocked users.
- Connecting Redis to store outdated user tokens (`black list`).
- Connecting Swagger to document API.

## Git Flow History
1. Initial Git Flow for project `git flow init`
2. The `feature/add-user-crud-v1.0.0` branch was completed and merged into `develop`.
3. The `feature/add-application-files-v1.0.0` branch was completed and merged into `develop`.
4. The `feature/update-user-crud-v1.1.0` branch was completed and merged into `develop`.
5. The `feature/update-application-files-v1.1.0` branch was completed and merged into `develop`.

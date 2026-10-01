# TaskFlow Android

TaskFlow is a reference Android application built with **Kotlin** and **Jetpack Compose** to demonstrate modern Android development and application architecture.

The application itself is intentionally simple: users can authenticate, manage projects, and manage tasks within those projects. The primary purpose of the project is not the product domain, but the architecture behind it.

TaskFlow explores how a real Android application can be structured around clear boundaries, unidirectional data flow, dependency inversion, local persistence, networking, authentication, and multi-module design.

The Android application communicates with a separate **Spring Boot backend**, allowing networking and authentication behavior to be implemented against a real API rather than mocked services.

---

## What This Project Demonstrates

TaskFlow currently demonstrates:

- Kotlin
- Jetpack Compose
- Material 3
- Unidirectional Data Flow (UDF)
- ViewModel
- StateFlow and SharedFlow
- Lifecycle-aware state collection
- Domain use cases
- Repository pattern
- Dependency inversion
- Hilt dependency injection
- Multi-module Android architecture
- Navigation Compose
- Retrofit
- OkHttp
- Kotlin Serialization
- REST API integration
- Room
- SQLite
- Offline-first data flow
- DataStore
- JWT authentication
- Access and refresh tokens
- Automatic token refresh
- Authentication-aware navigation
- HTTP and network error handling
- Local database migrations

TaskFlow is being developed incrementally as an architecture learning/reference project. Some production concerns are intentionally introduced only when the project reaches the relevant architectural problem.

---

## Application Features

TaskFlow currently supports:

- User registration
- User login
- Persistent authentication sessions
- Automatic access-token attachment
- Automatic access-token refresh
- Logout
- Project creation
- Project editing
- Project deletion
- Task creation
- Task editing
- Task completion
- Task deletion
- Clearing completed tasks
- Local Room persistence
- Synchronization between the backend and local database

Tasks belong to projects, and projects belong to authenticated users.

---

## Architecture

At a high level, a feature follows this dependency flow:

```text
Compose UI
    │
    │ events
    ▼
ViewModel
    │
    ▼
Use Cases
    │
    ▼
Repository Interface
    ▲
    │    
Repository Implementation
   ╱                      ╲
  ▼                        ▼
Local Data Source      Remote Data Source
  │                        │
  ▼                        ▼
Room                 Retrofit / OkHttp
                           │
                           ▼
                    TaskFlow Server
```

State flows in the opposite direction:

```text
Room
  │
  ▼
Flow
  │
  ▼
Repository
  │
  ▼
Use Case
  │
  ▼
ViewModel
  │
  ▼
StateFlow
  │
  ▼
Compose UI
```

The UI displays state and reports user events. It does not directly coordinate databases, networking, repositories, or navigation infrastructure.

---

## Unidirectional Data Flow

TaskFlow follows a UDF-style UI architecture.

A screen generally exposes an API similar to:

```kotlin
TaskScreen(
    state = state,
    onEvent = viewModel::onEvent,
)
```

The basic flow is:

```text
User Action
    │
    ▼
UiEvent
    │
    ▼
ViewModel
    │
    ▼
Domain / Data
    │
    ▼
UiState
    │
    ▼
Compose
```

Persistent screen state is owned by the ViewModel and exposed through `StateFlow`.

UI-only ephemeral state, such as dialog visibility, text-field drafts, or password visibility, can remain local to Compose where appropriate.

---

## Modules

TaskFlow is split into feature and core modules.

```text
TaskFlow
│
├── :app
│
├── :auth
│
├── :projects
│
├── :tasks
│
└── :core
    ├── :common
    ├── :database
    └── :network
```

### `:app`

The application entry point.

Responsibilities include:

- Application startup
- Top-level dependency wiring
- Navigation
- Authentication-aware navigation
- Coordination between otherwise independent features

Feature modules do not navigate directly to other feature modules. They emit events, while `:app` owns the navigation decision.

---

### `:auth`

Authentication and session management.

Responsibilities include:

- Registration
- Login
- Logout
- Authentication state
- Access-token storage
- Refresh-token storage
- Session persistence
- Token refresh integration

The module also implements networking abstractions required by `:core:network`, such as access-token provision and token refresh.

---

### `:projects`

Project management feature.

Responsibilities include:

- Displaying projects
- Creating projects
- Updating projects
- Deleting projects
- Project domain models
- Project use cases
- Project repository implementation
- Project local and remote data sources

---

### `:tasks`

Task management feature.

Responsibilities include:

- Displaying project tasks
- Creating tasks
- Updating tasks
- Completing tasks
- Deleting tasks
- Clearing completed tasks
- Task domain models
- Task use cases
- Task repository implementation
- Task local and remote data sources

---

### `:core:network`

Shared networking infrastructure.

Responsibilities include:

- Retrofit configuration
- OkHttp configuration
- Common HTTP interceptors
- Authentication interceptor
- OkHttp authenticator
- Token-related abstractions

The module does not depend on the authentication feature.

Instead, it defines abstractions that `:auth` implements.

This keeps the dependency direction pointing toward shared infrastructure rather than making the networking module aware of feature-specific authentication code.

---

### `:core:database`

The shared Room database and schema.

Responsibilities include:

- `TaskFlowDatabase`
- Room entities
- DAOs
- Database relationships
- Database migrations

The central database module owns the Room schema because the Room database must know about all entities participating in that schema.

Feature modules map database entities into their own domain models.

---

### `:core:common`

Low-level shared infrastructure that does not belong to a specific feature.

For example, TaskFlow uses this module for application-level coroutine infrastructure.

---

## Dependency Direction

One of the main architectural questions used throughout TaskFlow is:

> **Who should know this?**

Dependencies are intentionally kept directional.

For example:

```text
:tasks ───────────────► :core:database
      └───────────────► :core:network

:projects ────────────► :core:database
         └────────────► :core:network

:auth ────────────────► :core:network
     └────────────────► :core:common

:app ─────────────────► feature modules
```

Feature modules do not depend on each other simply to perform navigation or share implementation details.

---

## Local Data and Offline-First Design

Room acts as the local source of truth for application data.

A typical refresh looks like:

```text
Server
   │
   ▼
Remote Data Source
   │
   ▼
Repository
   │
   ▼
Room
   │
   ▼
Room Flow
   │
   ▼
Repository
   │
   ▼
ViewModel
   │
   ▼
Compose UI
```

The UI does not render a separate copy of network data after a refresh.

Instead, network data is persisted locally and the existing Room `Flow` emits the new state.

This gives the application a single observable source of truth for project and task data.

---

## Mutations

For server-backed mutations, TaskFlow currently follows a remote-first approach.

For example:

```text
User deletes task
      │
      ▼
ViewModel
      │
      ▼
Use Case
      │
      ▼
Repository
      │
      ├──► Delete from server
      │
      └──► Delete from Room
                  │
                  ▼
             Room Flow
                  │
                  ▼
                 UI
```

The local database is modified only after the corresponding server mutation succeeds.

This avoids presenting a local mutation as successful when the authoritative server operation failed.

Bulk operations are represented as bulk API operations where appropriate. For example, clearing completed tasks uses:

```http
DELETE /projects/{projectId}/tasks/completed
```

rather than sending one HTTP request for every completed task.

---

## Authentication

TaskFlow uses JWT-based authentication with:

- Access tokens
- Refresh tokens
- Persistent sessions
- Automatic access-token attachment
- Automatic access-token refresh

Normal API requests use an OkHttp authentication interceptor:

```text
Request
   │
   ▼
AuthenticationInterceptor
   │
   ├── no session ──► request unchanged
   │
   └── session ─────► Authorization: Bearer <access-token>
```

When an access token expires:

```text
Protected request
      │
      ▼
401 Unauthorized
      │
      ▼
OkHttp Authenticator
      │
      ▼
POST /auth/refresh
      │
      ▼
New access token
      │
      ▼
Retry original request
```

Refresh-token failure with `401 Unauthorized` invalidates the local authentication session.

Network failures and server failures do not automatically mean the user's authentication session is invalid.

---

## Separate Authentication HTTP Client

TaskFlow deliberately uses two Retrofit/OkHttp paths.

```text
Normal API client
├── application headers
├── authentication interceptor
├── HTTP logging
└── token authenticator

Authentication API client
├── application headers
└── HTTP logging
```

The authentication client does **not** contain the token authenticator.

This prevents a dependency and execution cycle such as:

```text
OkHttp
  ↓
Authenticator
  ↓
Token Refresher
  ↓
Auth Repository
  ↓
Auth API
  ↓
Retrofit
  ↓
OkHttp
```

It also prevents a failed refresh request from recursively trying to refresh itself.

---

## Backend

TaskFlow Android communicates with a separate Spring Boot application:

```text
taskflow-server
```

The server provides:

- REST endpoints
- User registration
- Login
- JWT access tokens
- JWT refresh tokens
- Authorization
- Project ownership
- Task ownership
- SQLite persistence

The backend is intentionally small. Its primary purpose is to provide realistic networking, persistence, authentication, authorization, and error-handling scenarios for the Android application.

---

## API Overview

The Android application currently uses endpoints including:

```text
Authentication

POST   /auth/register
POST   /auth/login
POST   /auth/refresh


Projects

GET    /projects
POST   /projects
GET    /projects/{id}
PATCH  /projects/{id}
DELETE /projects/{id}


Tasks

GET    /projects/{projectId}/tasks
POST   /projects/{projectId}/tasks
DELETE /projects/{projectId}/tasks/completed

GET    /tasks/{id}
PATCH  /tasks/{id}
DELETE /tasks/{id}
```

---

## Tech Stack

### Android

- Kotlin
- Jetpack Compose
- Material 3
- Android ViewModel
- Kotlin Coroutines
- Flow / StateFlow / SharedFlow
- Navigation Compose
- Hilt
- Room
- DataStore
- Retrofit
- OkHttp
- Kotlin Serialization

### Backend

The companion TaskFlow Server uses:

- Kotlin
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- SQLite
- JWT

---

## Running the Project

### Requirements

You will need:

- Android Studio
- Android SDK
- JDK compatible with the project's Gradle configuration
- A running TaskFlow Server instance

Clone the Android repository and open it in Android Studio.

Before running the application, start the TaskFlow backend.

---

## Backend URL

During development, TaskFlow connects directly to the locally running backend.

The Retrofit base URL must point to the machine running TaskFlow Server.

For example, a physical Android device on the same local network may use an address similar to:

```text
http://192.168.x.x:8080/
```

An Android emulator can normally reach the development machine through:

```text
http://10.0.2.2:8080/
```

Do not assume that an IP address committed for one development environment will work in another environment.

The current development configuration uses HTTP for local development. Production deployments should use HTTPS.

---

## Security Notes

TaskFlow is a reference/learning project, not a production-ready authentication system.

Production deployment would require additional work, including:

- HTTPS
- Secure server-side secret management
- Secure production JWT signing configuration
- Refresh-token revocation
- Server-side logout/session invalidation
- Refresh-token rotation
- Deliberate secure credential storage on Android
- Signing-key rotation
- Abuse/rate-limit controls where appropriate

Tokens and credentials must never be written to application logs.

---

## Project Status

TaskFlow is under active development as an Android architecture reference project.

Implemented areas currently include:

```text
✓ Compose UI
✓ UDF-style screen architecture
✓ ViewModel / StateFlow
✓ Use cases
✓ Repository abstraction
✓ Dependency injection
✓ Multi-module architecture
✓ REST networking
✓ Retrofit / OkHttp
✓ Room
✓ Database relationships
✓ Database migrations
✓ Local + remote repository coordination
✓ Projects
✓ Tasks
✓ Registration / login
✓ JWT authentication
✓ Access / refresh tokens
✓ Automatic token refresh
✓ DataStore session persistence
✓ Authentication-aware navigation
✓ Bulk task operations
```

Areas intended for further exploration include:

```text
□ Pagination
□ File upload
□ File download
□ WebSockets
□ Network caching
□ Additional DataStore patterns
□ Proto DataStore
□ Further offline-first synchronization strategies
```

---

## Design Goals

TaskFlow favors understandable architectural boundaries over adding abstraction for its own sake.

Some guiding principles are:

1. **UI displays state and reports events.**
2. **Domain code describes application operations rather than infrastructure details.**
3. **Repositories hide data-source coordination from callers.**
4. **Room provides an observable local source of truth.**
5. **Feature modules should remain independent where possible.**
6. **Shared infrastructure should not depend on feature implementations.**
7. **Navigation belongs at the application boundary rather than inside feature ViewModels.**
8. **Authentication failure and network failure are different states.**
9. **Architecture should solve concrete problems rather than exist only for diagram symmetry.**

---

## Repository Structure

A simplified view of the project:

```text
taskflow-android/
│
├── app/
│
├── auth/
│   └── src/main/java/com/example/taskflow/auth/
│       ├── data/
│       ├── di/
│       ├── domain/
│       └── ui/
│
├── projects/
│   └── src/main/java/com/example/taskflow/projects/
│       ├── data/
│       ├── di/
│       ├── domain/
│       └── ui/
│
├── tasks/
│   └── src/main/java/com/example/taskflow/tasks/
│       ├── data/
│       ├── di/
│       ├── domain/
│       └── ui/
│
├── core/
│   ├── common/
│   ├── database/
│   └── network/
│
├── gradle/
├── LICENSE
└── README.md
```

---

## Why TaskFlow?

Todo applications are simple enough that their business domain does not distract from architecture.

TaskFlow extends that idea slightly by introducing projects, authenticated users, local persistence, a real backend, and synchronization concerns.

That makes the application large enough to encounter real architectural problems while remaining small enough to understand as a complete system.

The goal is not to demonstrate the maximum number of patterns.

The goal is to understand **why a boundary exists, which component should own a responsibility, and what problem an abstraction is solving**.

---

## License

Copyright 2026 Khoa Ngo

Licensed under the Apache License, Version 2.0. See the
[LICENSE](LICENSE) file for details.
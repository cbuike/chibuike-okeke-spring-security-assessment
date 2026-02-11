# Spring Boot Multi-Module Security Project

This project demonstrates a **reusable authentication and authorization system** implemented with Spring Boot, modularized as a starter library and a sample application. It showcases **JWT-based authentication**, **role-based authorization**, and clean architecture principles.

---

## Author: Chibuike Okeke

## Table of Contents

- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)
- [Modules](#modules)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [API Testing](#api-testing)
- [Design Decisions](#design-decisions)

---

## Project Structure
```
chibuike-okeke-spring-security-assessment
├── pom.xml                         # Parent Maven POM (dependency & module management)
├── mvnw / mvnw.cmd                 # Maven Wrapper scripts
├── HELP.md                         # Spring Boot help documentation
├── README.md                       # Project documentation
│
├── core-security-starter/          # Reusable security starter module
│   ├── pom.xml                     # Starter module POM
│   └── src/
│       └── main/
│           └── java/               # Security configs, filters, utilities, shared logic
│
├── sample-application/             # Main Spring Boot application
│   ├── pom.xml                     # Application module POM
│   └── src/
│       ├── main/
│       │   ├── java/               # Controllers, services, repositories, domain logic
│       │   └── resources/          # application.yml/properties and static resources
│       └── test/
│           └── java/               # Unit and integration tests
│
└── .mvn/
    └── wrapper/                    # Maven Wrapper configuration
```

---

## Technology Stack

- Java 17
- Spring Boot 3.5
- Spring Security
- H2 In-Memory Database
- JWT (JSON Web Tokens)
- Maven
- Flyway Migration
- Lombok
- MockMvc / JUnit 5 for testing

---

## Modules

### 1. core-security-starter
A reusable library providing:

- JWT utilities (generate, validate, parse)
- JWT authentication filter
- Exception handling for 401 and 403
- Logging of authenticated user + endpoint
- Configuration properties (`jwt.secret`, `jwt.expiration`)
- Auto-configuration for Spring Security.

### 2. sample-application
Demonstrates the starter library with:

- REST endpoints
- In-memory H2 database for users
- Preloaded Users (`Alice`, 'Bob`)
- Preloaded roles (`ADMIN`, `USER`)
- Role-based authorization

---

## Getting Started

### Prerequisites
- Java 17
- Maven 3.8+
- Postman or cURL for testing

### Build Project

From the root directory:
```bash 
  mvn clean install 
```

### Configuration
JWT configuration in application.yml of sample-application can be 
overridden by your preferred values for secret and tokenExpiration.

```
app:
  security:
    jwt:
      secret: yourSecret
      expiration: 3600000 # 1 hour in milliseconds
```
**Note:**
Overriding is important if your secret key gets leaked or exposed, but you don't need to change when running this application, default 
values has already been set.


### Running The Application
1. From project root directory, change directory to sample application directory by running `cd sample-application`.
2. Run `mvn spring-boot:run` or `java -jar target/sample-application-1.0.0.jar` to start the application.
3. After executing the command, the application runs on http://localhost:8083

---

### API Endpoints
1. GET `http://localhost:8083/api/public/health` (Public Endpoint)
2. POST `http://localhost:8083/auth/login` (Authentication Endpoint)
```
  { 
    "username": "alice",
    "password": "password123"
   }
```
3. GET `http://localhost:8083/api/user/me` (Protected Endpoint for authenticated users)
4. GET `http://localhost:8083/api/admin/users` (Protected Endpoint for Role based Authorization)


### API Testing
Testing with Curl

Preloaded Users with Roles
```
- Username: "alice", Password: "password123", Role: ADMIN
- Username: "bob", Password: "password123", Role: USER
```

1. Public Endpoint Url
```
 curl -X 'GET' http://localhost:8083/api/public/health
```
Response: `OK`.

2. Authenticate / Login User
```
curl --location 'http://localhost:8083/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "username": "alice",
    "password": "password123"
}'
```
Response: `{ "access_token": <JWT_TOKEN> }`

3. Protected Endpoint with JWT (Method Level Access Control) on `http://localhost:8083/api/user/me`
```
curl --location 'http://localhost:8083/api/user/me' \
--header 'Authorization: Bearer <JWT_TOKEN>'
```
Response: `Authenticated user`

4. Protected Endpoint with JWT for Users with Admin Role (Role based authorization) on `http://localhost:8083/api/admin/users`
```
curl --location 'http://localhost:8083/api/admin/users' \
--header 'Authorization: Bearer <JWT_TOKEN>'
```
Response: `Admin users list`

Note: when you make use of Bob's JWT to Access  `http://localhost:8083/api/admins/users`, you will get a 403 forbidden error because
Bob isn't a user with Role of Admin, use JWT of Alice instead.

---

### Design Decisions

### Modular Architecture

The solution is split into two Maven modules:
- **`core-security-starter`** – a reusable Spring Boot starter containing all security and cross-cutting concerns
- **`sample-application`** – a thin consumer application demonstrating usage

All authentication, authorization, and cross-cutting logic live exclusively in the starter module.

**Why**  
This enforces a clear separation of concerns, keeps the sample application focused on business logic, and mirrors how shared internal 
libraries are typically built and reused across multiple services.

**Trade-off**  
Higher initial complexity compared to a single-module application, but significantly better long-term maintainability, consistency, and reuse.

---

### Spring Boot Starter & Auto-Configuration

The core security library uses Spring Boot auto-configuration to automatically register security filters, exception handlers, logging, and supporting beans.

**Why**  
Provides a plug-and-play experience for consuming applications and follows established Spring Boot conventions for starters.

**Trade-off**  
Auto-configuration can be harder to debug and requires careful use of conditional beans to remain flexible and non-intrusive.

---

### Stateless JWT Authentication

Username/password authentication is implemented using BCrypt password hashing. On successful login, a signed JWT is issued containing `userId`, `username`, `roles`, and `expiry`. 
JWT validation is performed by a servlet filter inside the starter.

**Why**  
JWT enables stateless authentication, scales well, and avoids server-side session management. Embedding roles allows authorization decisions without additional database lookups.

**Trade-off**  
JWT revocation is not immediate; tokens remain valid until expiration. Secret management and rotation must be handled carefully.

---

### Role-Based Authorization

Authorization is enforced using Spring Security with role-based access control at the URL and/or method level. The sample application demonstrates public, authenticated, and admin-only endpoints.

**Why**  
Centralizing authorization logic ensures consistent enforcement and prevents duplication or misconfiguration in consuming applications.

**Trade-off**  
Less flexibility for applications that require highly customized authorization logic without extending the starter.

---

### Centralized Cross-Cutting Concerns

The following concerns are implemented entirely in the core security starter:
- JWT handling and validation
- Authentication and authorization exception handling (401 / 403)
- Common error response format
- Logging of authenticated user and accessed endpoint
- Externalized configuration properties

**Why**  
This guarantees consistent behavior across applications, reduces boilerplate, and prevents security-critical logic from being reimplemented incorrectly.

**Trade-off**  
The starter is intentionally opinionated and may require extension points for advanced customization.

---

### Externalized Configuration

Security settings such as JWT secret and token expiry are exposed using strongly-typed `@ConfigurationProperties`.

**Why**  
Improves clarity, validation, IDE support, and operational flexibility while following Spring Boot best practices.

**Trade-off**  
Requires slightly more setup and documentation for consumers.

### Testing Strategy

Minimal integration tests using MockMvc validate authentication, authorization, and security filter behavior.

**Why**  
Integration tests provide confidence that the complete security pipeline works correctly, which is critical for security-sensitive code.

**Trade-off**  
Slower than pure unit tests, but offers higher confidence in real-world behavior.


### Scope & Security Balance

The implementation prioritizes correctness, clean architecture, and reusability over advanced features such as refresh tokens or token revocation.

**Why**  
Given the assessment scope and time constraints, the focus is on demonstrating strong fundamentals and production-ready structure.

**Trade-off**  
Some advanced security features are intentionally omitted but can be added later without architectural changes.

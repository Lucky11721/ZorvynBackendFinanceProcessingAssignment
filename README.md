# Zorvyn Finance Data Processing API

## 1. Project Overview
The Zorvyn Finance Data Processing API is an enterprise-grade backend system engineered to manage financial ledgers, user accounts, and real-time dashboard analytics. Built with a focus on data integrity, strict role-based security, and high-performance database interactions, this application demonstrates production-ready architectural patterns suitable for large-scale financial data processing.

## 2. Tech Stack
* **Language:** Java 17
* **Framework:** Spring Boot, Spring Security, Spring Data JPA
* **Database:** Microsoft SQL Server 2022
* **Authentication:** JSON Web Token (io.jsonwebtoken)
* **API Documentation:** Springdoc OpenAPI (Swagger UI), Postman
* **DevOps:** Docker, Docker Compose

## 3. Architecture & System Design Highlights
This project prioritizes maintainability and performance through several deliberate engineering decisions:

* **Manual Soft Deletes (Service-Layer):** Rather than relying on Hibernate's `@SQLDelete` annotations, soft deletes are handled explicitly within the business logic layer. This prevents hidden database "magic," makes unit testing straightforward, and easily facilitates the "Recycle Bin" feature without needing to bypass entity-level restrictions.
* **Database-Delegated Pagination:** Processing large datasets via Java Streams can lead to memory exhaustion (OOM errors). This system utilizes Spring Data's `Pageable` interface to push `OFFSET` and `FETCH` operations directly to the SQL Server, ensuring memory-safe data retrieval regardless of scale.
* **Startup Synchronization (Docker):** To prevent `ConnectionRefused` errors during automated deployments, the `docker-compose.yml` implements a custom health check. The API container actively pings the SQL Server via `sqlcmd` and waits to boot until the database is fully initialized and ready to accept connections.
* **Data Transfer Object (DTO) Pattern:** Strict separation is maintained between internal database entities and external API representations. This prevents the accidental exposure of sensitive data (like password hashes) and decouples the API contract from the database schema.

## 4. Features

### Core Features
* **Role-Based Access Control (RBAC):** Hierarchical access management utilizing `ADMIN`, `ANALYST`, and `VIEWER` permissions.
* **Stateless Authentication:** SHA-256 encrypted JWTs ensure secure, scalable session management without server-side state overhead.
* **Financial Ledger Management:** Comprehensive tracking of Income and Expense records.
* **Precision Mathematics:** Exclusive use of `BigDecimal` for all financial calculations to prevent floating-point rounding errors.

### Advanced Features
* **Audit-Compliant Soft Deletes:** Financial records are securely masked from the UI but preserved in the database for compliance.
* **Transaction Recovery:** Dedicated endpoint to restore accidentally deleted records, triggering automatic ledger recalculations.
* **Dynamic Filtering:** Search and filter transactions dynamically by type and category.
* **Atomic Transactions:** Spring's `@Transactional` boundaries guarantee that database writes (e.g., logging a transaction and updating a wallet balance) either succeed completely or roll back entirely, preventing orphaned data.

  
## Visual Overview Reference

To maintain a clean documentation structure, full-resolution screenshots of the application's operational state are stored in the repository for technical evaluation.

Please refer to the `/screenshots` folder for the following:

* **Swagger UI Overview:** Demonstrates the interactive OpenAPI 3.0 documentation and the JWT "Authorize" configuration.
* **Postman Test Suite:** Showcases various successful API calls, including the Dashboard Summary and Paginated Transaction views.
* **Security Validation:** Screenshots confirming 403 Forbidden responses when unauthorized roles attempt to access restricted administrative endpoints.
* **Soft Delete Verification:** Evidence of the 'deleted' flag being toggled in the database while the record remains persistent for audit trails.
## 5. Security Implementation
* **Endpoint Protection:** Spring Security intercepts all incoming requests, routing them through a custom JWT authentication filter.
* **Graceful Exception Handling:** Unauthorized attempts to access restricted endpoints are actively intercepted, returning clean `403 Forbidden` JSON responses rather than exposing stack traces.
* **Password Hashing:** Passwords are never stored in plaintext; they are securely hashed using BCrypt prior to database insertion.

## 6. Database Design
The system utilizes a highly optimized relational database schema in Microsoft SQL Server. 

A key design feature is the **1-to-1 Wallet (`TrackBalances`) System**. Instead of dynamically recalculating a user's total net balance by summing thousands of transaction rows on every dashboard load, the net balance is cached in a dedicated `TrackBalances` entity. Whenever a transaction is created, updated, or soft-deleted, atomic database operations update this wallet. This allows the API to fetch the user's current net balance in **O(1) time complexity**.

## 7. API Endpoints Summary

**Authentication & User Management**
* `POST /api/users/register` - Register a new user (Assigns initial role).
* `POST /api/auth/login` - Authenticate and retrieve a JWT Bearer token.
* `PATCH /api/users/{userId}/role` - Escalate or demote privileges (Admin only).
* `PATCH /api/users/{userId}/status` - Lock/Unlock user accounts (Admin only).
* `GET /api/users` - Retrieve a sanitized list of all users.

**Financial Ledger**
* `POST /api/transactions/user/{userId}` - Log a new financial record.
* `PUT /api/transactions/{transactionId}` - Update a record and synchronize balances.
* `DELETE /api/transactions/{transactionId}` - Soft-delete a record.
* `PATCH /api/transactions/{transactionId}/restore` - Recover a deleted record.
* `GET /api/transactions/user/{userId}` - Fetch paginated and filtered transaction history.
* `GET /api/transactions/user/{userId}/dashboard` - Fetch real-time total income, expenses, and net balance.

## 8. API Documentation

### Postman Collection
A comprehensive Postman workspace is provided, complete with pre-configured requests and saved responses detailing exact API behavior (including edge-case error handling).
* **Link:** [Zorvyn Finance Postman Workspace](https://lg1675682-310900.postman.co/workspace/Lucky-Gautam's-Workspace~3cd1c780-d475-4a55-8f65-d3d98c5cb835/collection/47531345-67094c40-ddd3-4b7e-8049-d108c16541c0?action=share&creator=47531345)

### Swagger / OpenAPI UI
The application dynamically generates an interactive API explorer.
* **Access:** Navigate to `http://localhost:8085/swagger-ui.html` while the server is running.
* **Authorization:** Generate a token via the `/api/auth/login` endpoint, click the "Authorize" button at the top of the Swagger UI, and paste the token to unlock secure endpoint testing.

## 9. Setup Instructions

### Step 1: Clone the Repository
```bash
git clone <your-repository-url>
cd ZorvynFinanceDataProcessing

### Step 2: Configure Application Properties
For security, credentials are not tracked in version control.
1. Navigate to `src/main/resources/`.
2. Locate `application.properties.example` and rename it to `application.properties`.
3. If running locally (without Docker), update the file with your local MS SQL Server credentials. Ensure an empty database named `BackendDataprocessing` exists.

### Step 3: Deployment Options

**Option A: Run via Docker Compose (Recommended)**
This method requires zero local installation of SQL Server. Docker will provision the database, apply the schema, and start the API automatically.
```bash
docker-compose up --build

Option B: Run Manually via Maven
Ensure your local SQL Server is running, then execute:

Bash
mvn spring-boot:run
The application will be accessible at http://localhost:8085.

10. Future Improvements
Caching: Implement Redis for frequently accessed static data (like user roles) to reduce database hits.

Asynchronous Processing: Introduce Apache Kafka or RabbitMQ to decouple notification services (e.g., email alerts for large transactions) from the core transaction thread.

Rate Limiting: Implement token-bucket rate limiting to protect endpoints from brute-force or DDoS attacks.

11. Author
Lucky Ahirwar Software Engineer | Backend Developer

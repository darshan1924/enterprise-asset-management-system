# Enterprise Asset Management System (EAMS)

A complete web application that helps companies manage their physical assets (like laptops, desktops, printers, etc.) throughout their entire life — from buying , giving to employees, repairing them, moving them between people/departments, and finally retiring.

It is built using Java 21, Spring Boot, Spring Security, Hibernate, MySQL, Thymeleaf, and Bootstrap. The application follows a clean layered structure, uses DTOs, has proper error handling, role-based access control, and keeps a full record of all important actions.

---

## Development Approach

This project was built by me as a Java Intern and Spring Boot learner.

I designed the overall architecture, database design, security model, and module structure.  
While developing, I used AI tools as an assistant to speed up certain parts of the work — mainly for generating UI templates and some repetitive backend code (DTOs, repository methods, etc.).

All AI-generated code was carefully reviewed, modified, integrated, and tested by me.  
I also handled debugging, fixing issues, connecting frontend with backend, and ensuring the complete application works end-to-end.

---

## AI Usage Disclosure

This project was developed with the assistance of AI tools.

- **Frontend** — The entire frontend (Thymeleaf templates, CSS, and JavaScript) was generated and refined using AI.

---

## Features

- **Dashboard** — live KPIs, category/status charts (Chart.js), recent activity feed, upcoming maintenance alerts
- **Department & Employee Management** — organizational hierarchy with headcount tracking
- **Vendor Management** — suppliers and maintenance service providers with ratings
- **Asset Inventory** — full lifecycle tracking (available → allocated → maintenance → retired), dynamic multi-field filtering
- **Allocation Workflow** — assign/return assets to employees, with automatic status synchronization
- **Maintenance Tickets** — raise, complete, or cancel service tickets with cost tracking
- **Asset Transfers** — department/employee-to-employee transfers with an approval workflow (pending → approved → completed / rejected)
- **Reports** — department-wise, asset inventory, and maintenance cost reports, with CSV export
- **Audit Log** — complete, immutable activity trail across every module
- **Role-Based Security** — `ADMIN`, `ASSET_MANAGER`, `EMPLOYEE` roles with URL and method-level authorization

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot, Spring MVC, Spring Data JPA, Spring Security |
| Database | MySQL 8 |
| ORM | Hibernate |
| Frontend | Thymeleaf, Bootstrap 5, vanilla JavaScript, Chart.js |
| Build | Maven |

---

## Architecture
```
Controller (REST + MVC)
|
▼
Service Layer ──── Custom Exceptions ──── Global Exception Handler
│
▼
Repository Layer (Spring Data JPA + Specifications)
│
▼
MySQL
```

- **DTO pattern** throughout — entities never leave the service layer
- **Layered validation** — Bean Validation on DTOs, custom validators for uniqueness/date-range rules
- **Centralized exception handling** via `@RestControllerAdvice`
- **Soft deletes** on all core entities
- **Full transactional integrity** on multi-entity operations (e.g., allocation updates both `AssetAllocation` and `Asset` status atomically)

---

## Getting Started

### Prerequisites

- Java 21 (JDK)
- Maven 3.9+
- MySQL 8.x running locally

### Setup

1. Clone the repository
```bash
   git clone https://github.com/<your-username>/enterprise-asset-management-system.git
   cd enterprise-asset-management-system
```

2. Create the database
```sql
   CREATE DATABASE eams;
```

3. Configure your local database credentials in `src/main/resources/application-dev.yml`
   (defaults to `root` / `root` — update if your local MySQL differs)

4. Run the application
```bash
   mvn spring-boot:run
```

5. Open your browser at `http://localhost:8080`

### Default Login

On first startup, an admin account is seeded automatically:

| Field | Value |
|---|---|
| Username | `admin` |
| Password | `Admin@123` |

**Change this password immediately after first login** via the profile menu → Change Password.

---

## Running in Production Mode

Production configuration pulls all secrets from environment variables — nothing is hardcoded.

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL="jdbc:mysql://<host>:3306/eams?useSSL=true&serverTimezone=UTC"
export DB_USERNAME=<your-db-username>
export DB_PASSWORD=<your-db-password>

mvn clean package -DskipTests
java -jar target/eams-0.0.1-SNAPSHOT.jar
```

Health check available at `/actuator/health`.

---

## Project Structure
```
src/main/java/com/company/eams
├── config — Security, pagination, seeding, JPA auditing
├── controller
│ ├── api — REST controllers (/api/**)
│ └── web — Thymeleaf MVC controllers
├── dto
│ ├── request — Validated input DTOs
│ └── response — Output DTOs
├── entity — JPA entities
├── enums — Domain enums
├── exception — Custom exceptions + global handler
├── mapper — Entity ↔ DTO conversion
├── repository — Spring Data JPA repositories + Specifications
├── security — Spring Security UserDetails adapter
├── service
│ ├── interfaces
│ └── impl
├── specification — Dynamic query filters
├── util — Shared utilities (CSV export, etc.)
└── validation — Custom Bean Validation annotations
```

---

## Roles & Permissions

| Role | Access |
|---|---|
| `ADMIN` | Full access — departments, employees, user management, audit log, all asset operations |
| `ASSET_MANAGER` | Can manage vendors, categories, assets, allocations, maintenance, and transfers; cannot manage departments/employees/users |
| `EMPLOYEE` | Read-only access across the system |

---

## Screenshots

*(Add screenshots of the Dashboard, Asset list, and a report page here before publishing)*

---

## License

This project was built as a portfolio/learning project.
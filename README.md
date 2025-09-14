# Consultorios – Backend (Spring Boot / WebFlux)

## Description
Reactive REST API for a medical appointments platform. Provides authentication with JWT, email confirmation, role-based authorization (ADMIN, PROFESSIONAL, PATIENT), professional profiles, and appointment management (create, update, cancel, export).

## Technologies
- Java 21, Maven
- Spring Boot 3 (WebFlux, Security, Validation, Cache)
- Spring Data R2DBC (reactive MySQL driver)
- Flyway (database migrations)
- JWT (Nimbus JOSE)
- MapStruct (DTO mapping), Lombok (boilerplate reduction)
- SLF4J + Logback (logging)
- springdoc-openapi (Swagger UI)
- MySQL 8

## Requirements
- JDK 21
- Maven 3.9+
- MySQL 8

## Installation
1. Clone the repository
   - `git clone <your-repo-url>`
2. Go to backend folder
   - `cd Task-S5.01-WebApplication`
3. Configure database and app settings via environment variables or `src/main/resources/application.yml`:
   - `DB_R2DBC_URL` (e.g., `r2dbc:mysql://localhost:3306/consultorios`)
   - `DB_JDBC_URL`  (e.g., `jdbc:mysql://localhost:3306/consultorios`)
   - `DB_USERNAME`, `DB_PASSWORD`
   - `APP_FRONTEND_BASE_URL` (default: `http://localhost:5173`)
   - `APP_MAIL_FROM` (default: `no-reply@example.com`)
   - `SECURITY.JWT.SECRET` (override `security.jwt.secret` if needed; 32+ chars)

## Run
Flyway runs on startup and applies migrations.

- Dev run (skip tests):
  - `mvn -DskipTests spring-boot:run`
- Build JAR:
  - `mvn clean package`
- Run JAR:
  - `java -jar target/*.jar`

Swagger UI (when enabled):
- `http://localhost:8080/swagger-ui/index.html`

## Deployment
1. Prepare production database and credentials.
2. Provide env vars (`DB_*`, `APP_*`, `SECURITY.JWT.*`).
3. Run the built JAR under a process manager (systemd, Docker, etc.).
4. Configure reverse proxy (optional) and TLS.

## Contributions
Contributions are welcome!
1. Fork the repository
2. Create a branch: `git checkout -b feature/NewFeature`
3. Commit: `git commit -m "feat: add NewFeature"`
4. Push: `git push origin feature/NewFeature`
5. Open a Pull Request


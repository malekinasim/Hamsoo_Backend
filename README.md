# Hamsoo — Backend (pilot version)

Backend of **Hamsoo**, a collaborative platform for designing, versioning and governing business processes in BPMN. Teams model processes together in the browser, organize them in a company-wide process tree, link them to roles, KPIs and documents, and send each new version through an approval flow before it becomes the official version.

This repository contains the **pilot version**, which I designed and built on my own (backend and frontend) at IRISA. Hamsoo later grew into a product offered as SaaS and on-premise, and development continued with a team after the pilot. The matching frontend is in [Hamsoo_frontend](https://github.com/malekinasim/Hamsoo_frontend).

## What the backend does

| Area | What it covers |
|---|---|
| **Process models and versions** | Models with versions; each version stores its BPMN XML and a rendered image. BPMN XML is read with the Camunda BPMN Model API, for example to find lanes and user tasks and link them to roles and people. |
| **Version approval** | Each model can have an ordered list of approvers (by level or group). A version goes through the approvers in order before it can become the official version, and every decision is logged. |
| **Real-time collaboration** | Several users can work on the same model version at once. Model updates, comments, role changes and presence (who joined or left) are broadcast over WebSocket/STOMP on a topic per model version, and WebSocket connections are authenticated. |
| **Process tree and catalogs** | Process categories and areas, an enterprise map, and catalogs for KPIs, inputs/outputs and resources that can be linked to model elements. |
| **Organizations and roles** | Organizations, units, posts, roles and members, used to assign process lanes and tasks to real people. A user can belong to several organizations and switch between them. |
| **Plans and features** | Sale plans and features control which capabilities each customer organization can use. |
| **Collaboration extras** | Comments on models, bookmarks and favourites, model documents and file uploads, and a form builder for process forms. |
| **Reports** | Generated reports such as a SIPOC report for a model version (JasperReports). |
| **Notifications** | Email (templated), SMS and Firebase push notifications, for example when a version is sent for approval, plus a live unread-notification counter over WebSocket. |
| **Identity and security** | Own sign-up and login with email/SMS verification, Google and GitHub sign-in (OAuth2), LDAP support, JWT access tokens with refresh tokens, and method-level permission checks per organization. |

## Tech stack

Java 17 · Spring Boot 2.6 · Spring Web · Spring Security (OAuth2 client and resource server, LDAP) · JWT · Spring Data JPA / Hibernate · PostgreSQL · WebSocket/STOMP · Camunda BPMN Model API · JasperReports · Spring Mail · Firebase Cloud Messaging · springdoc OpenAPI · Maven · Docker

## Project structure

```
src/main/java/com/hamsoo/bpm
├── controller/   REST and WebSocket endpoints, one package per area (model, modelversion, modelapprover, organ, saleplan, ...)
├── service/      business logic
├── repository/   Spring Data JPA repositories
├── model/        JPA entities and DTOs
├── security/     login providers and Camunda security filters
└── common/       configuration, security, BPMN helpers, exception handling, WebSocket config
```

## Running locally

Requirements: Java 17, Maven, PostgreSQL.

1. Create a PostgreSQL database and user that match `src/main/resources/application-dev.properties`, or override `spring.datasource.*`.
2. Provide secrets as environment variables. None are stored in the repository:

   | Variable | Used for |
   |---|---|
   | `HAMSOO_DB_PASSWORD` | Database password |
   | `HAMSOO_JWT_SECRET` | Signing key for access tokens |
   | `HAMSOO_MAIL_PASSWORD` | SMTP password for outgoing email |
   | `HAMSOO_SMS_PASSWORD` | SMS gateway password |
   | `HAMSOO_CAMUNDA_ADMIN_PASSWORD` | Camunda admin user |
   | `HAMSOO_SSL_ENABLED`, `HAMSOO_SSL_KEYSTORE`, `HAMSOO_SSL_KEYSTORE_PASSWORD`, `HAMSOO_SSL_KEY_PASSWORD` | Optional HTTPS with your own keystore (off by default) |

3. Run with the `dev` profile (the default):
   ```bash
   ./mvnw spring-boot:run
   ```
   The `dev` profile listens on port 8085 under the context path `/hamsoo`, and the OpenAPI/Swagger UI is enabled. The schema is created by Hibernate (`ddl-auto: update`).

A `Dockerfile` and `docker-compose.yml` are included for a containerized run (build the jar with `./mvnw package` first).

Notes:
- Firebase push and SMS need their own service credentials, which are not included.
- The log directory in `application-dev.properties` is a Windows path; change it on other systems.

## Status

This is a pilot snapshot kept as a portfolio project. It is not maintained as a running service, and automated tests are minimal (a context-load test only).

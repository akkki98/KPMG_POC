# Demo Spring Boot 3.0 Project

Generated Spring Boot 3.0 (3.0.12) project with Java 17.

## Included Dependencies
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- azure-messaging-servicebus
- spring-retry & spring-boot-starter-aop
- jjwt (api, impl, jackson)
- lombok
- h2 (runtime, dev only)

## Quick Start

1. Ensure Java 17 and Maven (3.8+) are installed.
2. (Optional) Generate a new Base64 secret and update `security.jwt.secret-key`.
3. Run:

```powershell
mvn spring-boot:run
```

Then open: http://localhost:8080/hello

## Auth Sample
POST http://localhost:8080/api/auth/login with JSON:
```json
{"username":"user", "password":"password"}
```
Returns a JWT token (demo only; in-memory user).

## Retry
`UserService#create` demonstrates a `@Retryable` method.

## Azure Service Bus
Provide a connection string (environment variable or `application.properties`). Example (PowerShell):
```powershell
$env:AZURE_SERVICEBUS_CONNECTION_STRING = "Endpoint=sb://..." # then run the app
```
Or set `azure.servicebus.connection-string` in properties (not recommended for production).

## Next Steps
- Replace in-memory auth with persistent users + password encoding.
- Add JWT authentication filter to secure endpoints with Bearer tokens.
- Add Service Bus sender / processor beans for real message operations.
- Add integration tests.

## Building
```powershell
mvn clean package
```

Produces `target/demo-0.0.1-SNAPSHOT.jar`.

## Notes
This scaffold is intentionally minimal; adjust versions / configuration per environment needs.

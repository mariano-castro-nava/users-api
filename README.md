# Users API — Chakray Java Technical Test

REST API for user management built with **Java 17**, **Spring Boot 3.2.5** and **Maven**.
Users are kept in an in-memory list (no database), as requested.

## Requirements

- JDK 17 (or 21)
- Maven 3.6.3+ (or use the included Dockerfile)

## Run

```bash
# build + run the tests
mvn clean verify

# start the API (http://localhost:8080)
mvn spring-boot:run
```

With Docker:

```bash
docker build -t users-api .
docker run -p 8080:8080 users-api
```

## Swagger / OpenAPI

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Click **Authorize** in Swagger UI and paste the JWT obtained from `/login` to call the secured `/users` endpoints.

## Authentication

Every `/users` endpoint is protected with a **Bearer JWT**. Only `/login` and the Swagger
resources are public.

1. `POST /login` with a `tax_id` as username and the password.
2. Use the returned `token` in the `Authorization: Bearer <token>` header.

Seed credentials (passwords are stored AES-256 encrypted; these are the plaintext values):

| username (tax_id) | password  |
|-------------------|-----------|
| `AARR990101XXX`   | password1 |
| `BBSS880202YYY`   | password2 |
| `CCTT770303ZZZ`   | password3 |

```bash
curl -X POST http://localhost:8080/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"AARR990101XXX","password":"password1"}'
```

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST   | `/login` | Authenticate (username = tax_id), returns a JWT |
| GET    | `/users?sortedBy=[email\|id\|name\|phone\|tax_id\|created_at]` | List users, optionally sorted |
| GET    | `/users?filter=[attribute]+[co\|eq\|sw\|ew]+[value]` | List users filtered |
| POST   | `/users` | Create a user |
| PATCH  | `/users/{id}` | Update user attributes by id |
| DELETE | `/users/{id}` | Delete a user by id |

`sortedBy` and `filter` can be combined. `sortedBy` may be empty/null (returns the list as-is).

### Filter operators

| op | meaning | example |
|----|---------|---------|
| co | contains    | `/users?filter=name+co+user` |
| eq | equals      | `/users?filter=tax_id+eq+AARR990101XXX` |
| sw | starts with | `/users?filter=phone+sw+55` |
| ew | ends with   | `/users?filter=email+ew+mail.com` |

> In a query string `+` is decoded to a space, so `filter=name+co+user` is received as `name co user`.

### Example: create a user

```bash
curl -X POST http://localhost:8080/users \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <token>' \
  -d '{
    "email": "user4@mail.com",
    "name": "user4",
    "phone": "+52 5599887766",
    "password": "password4",
    "tax_id": "DDUU660404WWW",
    "addresses": [
      { "id": 1, "name": "workaddress", "street": "street No. 99", "country_code": "MX" }
    ]
  }'
```

## Implementation notes (requirements mapping)

- **Password storage (AES-256):** passwords are encrypted with `AES/GCM/NoPadding` using a
  256-bit key derived from a configured secret (`Aes256Util`). The password is **never** returned
  in any response (the response DTO does not contain the field).
- **`created_at`:** generated as the current timestamp in the **Madagascar** time zone
  (`Indian/Antananarivo`, UTC+3) with the format `dd-MM-yyyy HH:mm`.
- **`tax_id` validation:** must match the **RFC** format
  (`[A-Z&Ñ]{3,4}` + 6 date digits + 3 char homoclave, e.g. `AARR990101XXX`).
- **`tax_id` uniqueness:** enforced on create and update (HTTP 409 on conflict).
- **Phone validation ("AndresFormat"):** custom `@AndresFormat` constraint. The national number
  (after removing an optional `+countrycode` prefix and whitespace) must be exactly **10 digits**.
- **JSON naming:** `tax_id`, `created_at`, `country_code` use snake_case via the global Jackson
  `SNAKE_CASE` strategy.

## Extras included

- **Unit tests** — JUnit 5 + Mockito (`mvn test`): AES utility, validators, `UserService`
  (filter/sort/CRUD/uniqueness) and `AuthService`.
- **Swagger / OpenAPI** — springdoc.
- **Postman collection** — `postman/Users-API.postman_collection.json`
  (run *Login* first; the token is stored and reused automatically).
- **Docker** — multi-stage `Dockerfile`.
- **Git** — versioned with git.

## Configuration

See `src/main/resources/application.yml` (AES secret, JWT secret + expiration, time zone, format).
Change the secrets before any real deployment.

## Project layout

```
src/main/java/com/chakray/usersapi
├── config        # security, OpenAPI, data seeder
├── controller    # UserController, AuthController
├── dto           # request/response payloads
├── exception     # custom exceptions + global handler
├── model         # User, Address
├── repository    # in-memory store
├── security      # AES-256, JWT, JWT filter
├── service       # UserService, AuthService, mapper
└── validation    # RFC pattern, AndresFormat constraint
```

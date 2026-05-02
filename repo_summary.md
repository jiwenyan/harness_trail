# Repository Summary: `harnesseng_trail` (Food Ordering Backend)

**Tech Stack:** Spring Boot 3.5.0 / Jersey / MongoDB / Java 25 / Maven

## Lines of Code

| Category | Lines |
|---|---|
| Production Java | 5,177 |
| Test Java | 2,570 |
| **Total Java** | **7,747** |
| Non-Java source files (XML, YML, SH, PY, MD) | 13 |

## Classes

| Category | Count |
|---|---|
| Production Java files | **46** |
| Test Java files | **13** |
| Modules | **4** (data-module, dao-module, service-module, api-module) |

## Test Cases

| Module | Test Classes | Tests |
|---|---|---|
| dao-module | 5 | 67 |
| service-module | 4 | 86 |
| api-module | 4 | 45 |
| **Total** | **13** | **198** (all passing, 0 failures) |

## REST API Endpoints: 43 total

| HTTP Method | Count | Per Resource |
|---|---|---|
| **GET** | 23 | FoodItemResource: 6, HealthResource: 1, OrderResource: 6, UserResource: 10 |
| **POST** | 5 | FoodItemResource: 1, OrderResource: 1, UserResource: 3 |
| **PUT** | 11 | FoodItemResource: 3, OrderResource: 5, UserResource: 3 |
| **DELETE** | 4 | FoodItemResource: 1, OrderResource: 1, UserResource: 2 |

## Architecture (4-layer)

- **data-module** — 6 entities, 2 enums, 1 constants
- **dao-module** — 5 interfaces, 5 DAO implementations (MongoTemplate-based)
- **service-module** — 4 service interfaces, 4 service implementations
- **api-module** — 4 REST resources, 5 request DTOs, 4 response DTOs, exception handling, CORS config

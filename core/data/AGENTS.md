# Module Rules for AI Assistant (`:core:data`)

## Overview & Module Scope
The `:core:data` module is responsible for data persistence, network operations, key-value storage, and data mapping. It provides concrete implementations of the repository interfaces defined in `:core:domain`.

- **Module Path:** `:core:data`
- **Package:** `com.company.template.core.data`
- **Dependencies:** Depends on `:core:domain`. Must **NOT** depend on `:core:presentation`, `:core:designsystem`, or `:feature:*`.

---

## Strict Architectural Boundaries

1. **DTO & Entity Encapsulation**
   - Room `@Entity` classes, Ktor `@Serializable` DTOs, and DataStore Preference keys **MUST NOT** be exposed outside `:core:data`.
   - All external methods in repositories must return **Domain Models** defined in `:core:domain`.

2. **Repository Implementation**
   - Every repository in `:core:data` must implement an interface from `:core:domain.repository` (e.g. `FooRepositoryImpl : FooRepository`).

3. **Data Mapping**
   - Always map DTOs and Room Entities to Domain Models before returning them to `:core:domain` or `:core:presentation`.
   - Keep mappers as extension functions in `com.company.template.core.data.mapper`.

4. **Exception Mapping & Result Wrappers**
   - Catch network exceptions (Ktor `ResponseException`, `ConnectTimeoutException`, platform sockets) and local database errors within `:core:data`.
   - Use `platformSafeCall` for HTTP requests to map exceptions and status codes to `Result<T, DataError.Remote>`.
   - Always invoke `coroutineContext.ensureActive()` in generic catch blocks to preserve coroutine cancellation.

---

## Package Structure

```
com.company.template.core.data/
├── database/            # Room Database setup, DAOs, Entities
│   ├── dao/             # Room DAOs (e.g., UserDao.kt)
│   ├── entity/          # Room @Entity data classes
│   ├── AppDatabase.kt   # RoomDatabase definition
│   └── DatabaseFactory.kt
├── network/             # Ktor HTTP Client & API Services
│   ├── api/             # Ktor API interfaces/clients
│   ├── model/           # Network DTOs (@Serializable)
│   ├── HttpClientFactory.kt # Ktor HttpClient configuration & plugins
│   ├── HttpClientExt.kt     # GET/POST/PUT/DELETE helpers & platformSafeCall
│   └── UrlConstants.kt
├── datastore/           # DataStore Preferences
│   └── CreateDataStore.kt
├── repository/          # Implementation of Domain Repositories
│   └── UserRepositoryImpl.kt
├── mapper/              # Entity/DTO <-> Domain Model mappers
│   └── UserMapper.kt
└── di/                  # Koin DI module
    └── DataModule.kt
```

---

## Subsystem Best Practices

### 1. Room Database (KMP)
- Use `@ConstructedBy(AppDatabaseConstructor::class)` for multiplatform Room initialization.
- DAOs must use `Flow<T>` for reactive queries and `suspend` for one-shot operations.
- Ensure all DAO operations execute on `Dispatchers.IO`.

### 2. Networking (Ktor 3.x)
- Define DTOs in `network/model/` annotated with `@Serializable`.
- Use type-safe HTTP extensions (`httpClient.get`, `httpClient.post`) which wrap requests in `platformSafeCall` and map HTTP status codes to `Result<T, DataError.Remote>`.
- Configure Ktor `Logging` via Kermit (`co.touchlab.kermit.Logger`) and `ContentNegotiation` with `Json` (`ignoreUnknownKeys = true`).

### 3. Key-Value Storage (DataStore Preferences)
- Keep Preference keys internal or encapsulated in dedicated DataSource classes.
- Map preference flows directly to Domain types/models.

### 4. Asynchronous Processing & Threading
- Execute IO operations on `Dispatchers.IO`.
- Do NOT leak `MutableStateFlow` or `MutableSharedFlow` outside DataSources or Repositories. Expose read-only `Flow` or `StateFlow`.

---

## Command Execution & AI Tooling Rules
- **Gradle & Shell Commands:** Always run Gradle tasks and CLI commands in quiet mode using `--quiet` (or `-q`) flag (e.g. `./gradlew assembleDebug --quiet`) to minimize log output clutter.

---

## Dependency Injection (Koin)
- Register all DataSources, DAOs, Ktor clients, and Repository implementations in `dataModule`:
  ```kotlin
  val dataModule = module {
      includes(targetDataModule)
      single<AppDatabase> { getRoomDatabase(get()) }
      single { get<AppDatabase>().userDao() }
      single { HttpClientFactory() }
      single<HttpClient> { get<HttpClientFactory>().create(engine = get()) }
      single<UserRepository> { UserRepositoryImpl(dao = get(), api = get()) }
  }
  ```

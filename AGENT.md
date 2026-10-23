# Project Rules for AI Assistant (STO Project)

## Project Overview & Tech Stack
- **Project Name:** STO
- **Architecture:** Multi-module Clean Architecture + Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP)
- **Root Package:** `com.bystrov.sto`
- **Build System:** Gradle Kotlin DSL + Type-Safe Project Accessors (`projects.core.domain`) + Version Catalog (`gradle/libs.versions.toml`)
- **Convention Plugins:** Located in `build-logic/convention`

## Core Technical Stack
- **UI:** Compose Multiplatform, Material 3
- **Dependency Injection:** Koin (`koin-core`, `koin-compose`, `koin-compose-viewmodel`)
- **Database:** Room KMP + KSP + Bundled SQLite
- **Networking:** Ktor Client 3.x + `kotlinx.serialization`
- **Key-Value Storage:** DataStore Preferences
- **Asynchronous Processing:** Kotlin Coroutines + `StateFlow` / `SharedFlow`
- **Navigation:** Navigation 3 / Compose Navigation Multiplatform
- **Image Loading:** Coil 3

---

## Architectural Rules & Module Isolation

1. **`:core:domain` (Pure Domain Layer)**
   - Must contain ONLY pure Kotlin code (Entities, Repository Interfaces, Use Cases).
   - Strict Prohibition: NO Android, Compose, Room, DataStore, or Ktor dependencies here.
   - Uses `alias(libs.plugins.convention.kmp.library)`.

2. **`:core:data` (Data Layer)**
   - Implements Repository interfaces defined in `:core:domain`.
   - Contains Room DAOs & Entities, Ktor API clients, DataStore DataSources.
   - Maps DTOs and Room Entities to Domain Models before returning to Domain.
   - Uses `alias(libs.plugins.convention.kmp.library)` and `alias(libs.plugins.convention.room)`.

3. **`:core:designsystem` (UI Design System)**
   - Contains custom Material 3 theme, colors, typography, shapes, and reusable UI components.
   - Uses `alias(libs.plugins.convention.cmp.library)`.

4. **`:core:presentation` (Shared Presentation Layer)**
   - Contains shared UI State abstractions, base ViewModel classes, and reusable presentation UI.
   - Uses `alias(libs.plugins.convention.cmp.library)`.

5. **`:feature:*` (Feature Modules)**
   - Independent feature modules (e.g. `:feature:home`, `:feature:profile`).
   - Uses `alias(libs.plugins.convention.cmp.feature)`.
   - Depends on `:core:domain`, `:core:designsystem`, `:core:presentation`.

6. **`:androidApp` (Android Entry Point)**
   - Uses `alias(libs.plugins.convention.android.application.compose)`.
   - Assembles application dependencies and sets up root Activity/App entry point.

---

## Convention Plugins & Dependency Management

- **NEVER** hardcode version strings or library coordinates directly inside module `build.gradle.kts` files.
- **ALWAYS** declare new dependencies or versions in `gradle/libs.versions.toml`.
- **ALWAYS** use type-safe project accessors when referencing modules (`implementation(projects.core.domain)` instead of `project(":core:domain")`).
- **ALWAYS** apply Convention Plugins in new or existing modules:
  - `alias(libs.plugins.convention.kmp.library)` — for KMP libraries.
  - `alias(libs.plugins.convention.cmp.library)` — for CMP UI libraries.
  - `alias(libs.plugins.convention.cmp.feature)` — for feature UI modules.
  - `alias(libs.plugins.convention.room)` — for modules requiring Room DB.
  - `alias(libs.plugins.convention.android.application.compose)` — for Android app module.

---

## Coding Standards & Idioms

### Kotlin & Coroutines
- Use idiomatic, modern Kotlin features.
- Prefer `val` over `var` and immutable data structures.
- Use explicit return types for public functions and properties.
- State Flow: Do NOT expose `MutableStateFlow` or `MutableSharedFlow` outside ViewModel/Repository. Always expose `asStateFlow()` or `asSharedFlow()`.
- Explicitly pass `CoroutineDispatcher` where appropriate (`Dispatchers.IO`, `Dispatchers.Default`).

### Compose UI
- All Composable functions should accept an optional `modifier: Modifier = Modifier` as their first optional parameter.
- Pass state down and events up (State Hoisting).
- Use `collectAsStateWithLifecycle()` when consuming StateFlow in Compose UI.
- Use `@Preview` annotations for Compose components.

### Dependency Injection (Koin)
- Define Koin modules per Gradle module (e.g., `val dataModule = module { ... }`).
- Inject ViewModels in Compose using `koinViewModel()`.

---

## File Creation & Refactoring Rules for AI
- Keep `build.gradle.kts` files concise (5–15 lines max) by leveraging convention plugins.
- Keep package structure aligned with project path: `com.bystrov.sto.<module_path_with_dots>`.

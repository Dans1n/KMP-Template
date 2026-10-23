# Kotlin Multiplatform Clean Architecture Template

Готовый шаблон и стартовый комплект для создания кроссплатформенных мобильных приложений на **Kotlin Multiplatform (KMP)** и **Compose Multiplatform (CMP)** с архитектурой **Clean Architecture**.

---

## Быстрый старт: Инициализация нового проекта из шаблона

Этот репозиторий настроен как шаблон. Чтобы создать на его основе новый проект:

### 1. Переименование проекта и пакетов
Запустите скрипт инициализации `scripts/init_project.py` в терминале:

```bash
python3 scripts/init_project.py --name "MyAwesomeApp" --package "com.company.myawesomeapp"
```

Скрипт автоматически:
- Заменит root package (`com.bystrov.sto` $\to$ `com.company.myawesomeapp`).
- Заменит название проекта (`STO` $\to$ `MyAwesomeApp`).
- Переместит исходный код в соответствующие каталоги пакета.
- Обновит `settings.gradle.kts`, `libs.versions.toml`, `build-logic`, `AGENT.md` и iOS-конфигурации.

---

## Генерация новых фиче-модулей (`:feature:*`)

Для автоматического создания изолированного фиче-модуля запустите Gradle-таску:

```bash
./gradlew createFeatureModule -Pname=profile
```

Таска автоматически:
1. Создаст модуль `:feature:profile` в папке `feature/profile`.
2. Настроит `build.gradle.kts` с применением плагина `alias(libs.plugins.convention.cmp.feature)`.
3. Сгенерирует стартовые файлы:
   - `ProfileScreen.kt` (Compose UI)
   - `ProfileViewModel.kt` (StateFlow & UI State)
   - `ProfileModule.kt` (Koin DI module)
4. Пропишет `include(":feature:profile")` в `settings.gradle.kts`.

---

## Архитектура и структура модулей

```
.
├── androidApp/               # Входная точка Android приложения
├── iosApp/                   # Xcode проект для iOS
├── shared/                   # Общий модуль инициализации (Koin Init, App entry point)
├── core/
│   ├── domain/               # Чистая бизнес-логика (Entities, Repository Interfaces)
│   ├── data/                 # Реализация данных (Room DB, Ktor, DataStore, Mappers)
│   ├── designsystem/         # Material 3 тема, стили и UI-компоненты
│   └── presentation/         # Базовые UI State абстракции и ViewModels
├── feature/                  # Независимые фиче-модули
│   └── <feature_name>/       # Compose UI, ViewModels, Navigation, Koin DI
└── build-logic/              # Gradle Convention Plugins (Type-safe сборка)
```

---

## Сборка и запуск

### Android App
```bash
./gradlew :androidApp:assembleDebug
```

### iOS App
Откройте `/iosApp` в Xcode и запустите на симуляторе или устройстве.

### Запуск тестов
```bash
./gradlew check
```

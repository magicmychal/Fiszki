# Copilot Instructions

This file provides guidance to GitHub Copilot when working with code in this repository.

## Build & Development Commands

```bash
# Build the project
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Run all unit tests
./gradlew test

# Run a single test class
./gradlew test --tests "eu.qm.fiszki.AlgorithmTest"

# Clean build
./gradlew clean build

# Check dependencies
./gradlew dependencies
```

Requires JDK 11+ (Kotlin JVM toolchain targets Java 11). Uses Gradle wrapper (no global Gradle needed).

## Architecture Overview

**Fiszki** is an Android flashcard learning app (`eu.qm.fiszki`). It targets SDK 35 (min SDK 31) and uses Kotlin, AndroidX, and Material Design 3.

### Layer Structure

- **Models** (`model/`): `Flashcard` and `Category` are ORMLite-annotated data classes persisted to SQLite. Each has a `Repository` class (DAO pattern via `OrmLiteSqliteOpenHelper`) and a `Validation` class for input checking.
- **Database** (`database/ORM/`): `DBHelper` extends `OrmLiteSqliteOpenHelper`, manages schema migrations (currently version 5). `DBConfigUtility` generates ORMLite config. Config file at `res/raw/ormlite_config.txt`.
- **Navigation**: Single-Activity architecture via `NavHostActivity` with bottom navigation. Fragments for main tabs: `LearningFragment`, `ExamFragment`, `CategoryFragment`. Compose UI used in learning/exam screens.
- **Activities** (`activity/`): `CheckActivity` (notification answer checking), `LearningCheckActivity`, `ExamCheckActivity`, `ChatActivity`, flashcard/category management via `myWords/`.
- **Dialogs** (`dialogs/`): Material dialog classes organized by feature (flashcard CRUD, category CRUD, check results, exam settings, learning settings). Uses `afollestad:material-dialogs:0.9.6.0` (pre-AndroidX, converted via Jetifier).
- **Algorithm** (`algorithm/`): Flashcard selection logic. `Algorithm.drawCardAlgorithm()` currently uses random selection. `PriorityCount`, `MultiplierPoints`, and `Drawer` exist for priority-based weighted selection (not yet fully wired in).
- **Listeners** (`listeners/`): Click/long-click handlers for flashcard list items and exam actions, separated from activities.

### Key Singletons & Utilities

- `CategoryManagerSingleton`: Tracks currently selected category across activities
- `SelectedFlashcardsSingleton`: Tracks multi-selected flashcards for bulk operations
- `LocalSharedPreferences`: Wrapper around Android SharedPreferences (notification state, frequency, night mode, first-run flag)
- `NightModeController`: Applies light (`AppTheme`), dark (`NightMode`), or yellow theme variants in every activity's `onCreate()`
- `Checker`: Compares user answers to flashcard values (exact string match, case-insensitive)
- `HapticFeedback`: Vibration utility for correct (light, 30ms) and wrong (strong, 200ms) answer feedback

### Data Flow

Flashcards belong to categories. Categories have `langFrom`/`langOn` fields for language pair. The `chosen` field on Category controls which categories are active for learning/exam modes. Learning mode uses the algorithm to draw cards; exam mode uses configurable ranges and repeat settings.

### Notification System

`AlarmReceiver` uses `AlarmManager` with configurable repeat intervals (1/5/15/30/60 min). Notifications launch `CheckActivity` on tap. Channel ID: `fiszki_notifications`.

## Key Dependencies

- **ORMLite** 5.7: SQLite ORM (annotations on model classes, DAO pattern)
- **material-dialogs** 0.9.6.0: Dialog framework (Jetifier-converted)
- **Jetpack Compose**: Used for learning/exam screen UI (`LearningScreen`, `TitleFonts`)
- **Google Fonts (Compose)**: Roboto Flex, Roboto Mono, Roboto Serif, Porter Sans Block
- **JUnit 4**: Unit testing

## Coding Guidelines

- **Kotlin only**: All new code must be written in Kotlin. Do not introduce Java files. When modifying existing code, refactor touched files to idiomatic Kotlin if not already.
- **Compose for new UI**: New screens and UI features should use Jetpack Compose. Existing XML layouts may remain but prefer Compose for new work.
- **Theme-aware colors**: Never hardcode colors. Use `?attr/colorPrimary`, `?attr/colorOnSurface`, `?attr/colorSurfaceContainerHigh`, etc. to support light/dark/yellow themes. In dialogs, resolve theme attributes dynamically via `TypedValue` + `theme.resolveAttribute()`.
- **Localization**: All user-facing strings must be in `res/values/` (English) and `res/values-pl/` (Polish). Use `getString(R.string.…)` with format args, never hardcoded text.
- **Material Design 3**: Follow M3 guidelines. Use `MaterialAlertDialogBuilder` for new dialogs, M3 components and tokens.

## Migration Status

The codebase was converted from Java to Kotlin. All source files are Kotlin. ORMLite annotations use `@DatabaseField(columnName = "...")` with explicit column names to avoid Java-to-Kotlin field naming mismatches. From now on, all new features must be written in Kotlin and existing code should be refactored to idiomatic Kotlin when touched.

## Localization

Supports English (default) and Polish (`values-pl/`). String resources split across feature-specific files (e.g. `learning_strings.xml`, `category_strings.xml`, `exam_strings.xml`).

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

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

**Fiszki** is an Android flashcard learning app (`eu.qm.fiszki`). It targets SDK 35 (min SDK 24) and uses Kotlin, AndroidX, and Material Design 3.

### Layer Structure

- **Models** (`model/`): `Flashcard` and `Category` are ORMLite-annotated data classes persisted to SQLite. Each has a `Repository` class (DAO pattern via `OrmLiteSqliteOpenHelper`) and a `Validation` class for input checking.
- **Database** (`database/ORM/`): `DBHelper` extends `OrmLiteSqliteOpenHelper`, manages schema migrations (currently version 5). `DBConfigUtility` generates ORMLite config. Config file at `res/raw/ormlite_config.txt`.
- **Activities** (`activity/`): Standard Android activities — `MainActivity` (home with drawer nav + FAB), `CheckActivity` (answer checking), learning mode activities, exam mode activities, flashcard/category management via `myWords/`.
- **Dialogs** (`dialogs/`): Material dialog classes organized by feature (flashcard CRUD, category CRUD, check results, exam settings, learning settings). Uses `afollestad:material-dialogs:0.9.6.0` (pre-AndroidX, converted via Jetifier).
- **Algorithm** (`algorithm/`): Flashcard selection logic. `Algorithm.drawCardAlgorithm()` currently uses random selection. `PriorityCount`, `MultiplierPoints`, and `Drawer` exist for priority-based weighted selection (not yet fully wired in).
- **Drawer** (`drawer/`): Navigation drawer built with `MaterialDrawer` library. Items include category selection, notification toggle, frequency settings, night mode, and version display.
- **Listeners** (`listeners/`): Click/long-click handlers for flashcard list items and exam actions, separated from activities.

### Key Singletons & Utilities

- `CategoryManagerSingleton`: Tracks currently selected category across activities
- `SelectedFlashcardsSingleton`: Tracks multi-selected flashcards for bulk operations
- `LocalSharedPreferences`: Wrapper around Android SharedPreferences (notification state, frequency, night mode, first-run flag)
- `NightModeController`: Applies light (`AppTheme`) or dark (`NightMode`) theme in every activity's `onCreate()`
- `Checker`: Compares user answers to flashcard values (exact string match, case-insensitive)

### Data Flow

Flashcards belong to categories. Categories have `langFrom`/`langOn` fields for language pair. The `chosen` field on Category controls which categories are active for learning/exam modes. Learning mode uses the algorithm to draw cards; exam mode uses configurable ranges and repeat settings.

### Notification System

`AlarmReceiver` uses `AlarmManager` with configurable repeat intervals (1/5/15/30/60 min). Notifications launch `CheckActivity` on tap. Channel ID: `fiszki_notifications`.

## Key Dependencies

- **ORMLite** 5.7: SQLite ORM (annotations on model classes, DAO pattern)
- **MaterialDrawer** 6.1.2: Navigation drawer (Jetifier-converted)
- **material-dialogs** 0.9.6.0: Dialog framework (Jetifier-converted)
- **SlidingTutorial** 0.9.5: First-run tutorial (Jetifier-converted)
- **JUnit 4**: Unit testing

## Migration Status

The codebase was recently converted from Java to Kotlin (branch `2026-update`). All 80 source files have been converted. ORMLite annotations use `@DatabaseField(columnName = "...")` with explicit column names to avoid Java-to-Kotlin field naming mismatches.

## Localization

Supports English (default) and Polish (`values-pl/`). String resources in `res/values/strings.xml`.

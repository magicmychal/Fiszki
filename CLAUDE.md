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

**Fiszki** is an Android flashcard learning app (`eu.qm.fiszki`). It targets SDK 35 (min SDK 31) and uses Kotlin, AndroidX, and Material Design 3.

### Layer Structure

- **Models** (`model/`): `Flashcard` and `Category` are ORMLite-annotated data classes persisted to SQLite. Each has a `Repository` class (DAO pattern via `OrmLiteSqliteOpenHelper`) and a `Validation` class for input checking.
- **Database** (`database/ORM/`): `DBHelper` extends `OrmLiteSqliteOpenHelper`, manages schema migrations (currently version 5). `DBConfigUtility` generates ORMLite config. Config file at `res/raw/ormlite_config.txt`.
- **Navigation**: Single-Activity architecture via `NavHostActivity` with bottom navigation. Fragments for main tabs: `LearningFragment`, `ExamFragment`, `CategoryFragment`. Compose UI used in learning/exam/chat screens and flashcard list items.
- **Activities** (`activity/`): `CheckActivity` (notification answer checking), `LearningCheckActivity`, `ExamCheckActivity`, `ChatActivity` (fully Compose), flashcard/category management via `myWords/`.
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
- **Jetpack Compose**: Primary UI toolkit. Used for learning setup (`LearningScreen`), exam setup (`ExamScreen`), chat mode (`ChatScreen`), flashcard list items, and theming (`ComposeTheme`/`FiszkiTheme`)
- **Google Fonts (Compose)**: Roboto Flex, Roboto Mono, Roboto Serif, Porter Sans Block
- **MaterialDrawer** 8.4.5: Navigation drawer (mikepenz library, still XML-based)
- **JUnit 4**: Unit testing

## Coding Guidelines

- **Kotlin only**: All new code must be written in Kotlin. Do not introduce Java files. When modifying existing code, refactor touched files to idiomatic Kotlin if not already.
- **Jetpack Compose is the default**: All new UI must be built with Jetpack Compose. Do not create new XML layouts or Android drawables. When modifying an existing screen, migrate it to Compose if feasible. Use `FiszkiTheme` (from `ComposeTheme.kt`) to wrap all Compose content so it inherits the current light/dark/yellow theme. For activities that still use XML, embed Compose via `ComposeView` or `setContent {}`.
- **No new XML drawables**: Use Compose equivalents instead of creating drawable XML files. For shapes use `RoundedCornerShape`, `CircleShape`, `Surface`, or `Canvas`. For icons use `Icons.Default.*` / `Icons.AutoMirrored.*` from `material-icons-extended` or `painterResource()` only when a custom vector is truly needed. For gradients use `Brush.verticalGradient()` / `Brush.horizontalGradient()`.
- **Theme-aware colors**: Never hardcode colors. In Compose, use `MaterialTheme.colorScheme.*` (e.g. `MaterialTheme.colorScheme.primary`, `MaterialTheme.colorScheme.onSurface`). In remaining XML, use `?attr/colorPrimary`, `?attr/colorOnSurface`, `?attr/colorSurfaceContainerHigh`, etc. In dialogs, resolve theme attributes dynamically via `TypedValue` + `theme.resolveAttribute()`.
- **Localization**: All user-facing strings must be in `res/values/` (English) and `res/values-pl/` (Polish). In Compose use `stringResource(R.string.…)`, in Activities use `getString(R.string.…)` with format args. Never use hardcoded text.
- **Material Design 3**: Follow M3 guidelines. Use `MaterialAlertDialogBuilder` for new dialogs, M3 components and tokens.

## Migration Status

### Java → Kotlin (complete)
All source files are Kotlin. ORMLite annotations use `@DatabaseField(columnName = "...")` with explicit column names to avoid Java-to-Kotlin field naming mismatches.

### XML/Drawables → Jetpack Compose (in progress)
The codebase is actively migrating from XML layouts and Android drawables to Jetpack Compose. Current state:

**Fully Compose screens:**
- `LearningActivity` — uses `ComposeView` with `PracticeSetupScreen`
- `ExamActivity` — uses `ComposeView` with `ExamSetupScreen`
- `ChatActivity` — fully Compose via `setContent {}` with `ChatScreen`
- `FlashcardShowAdapter` — list items rendered via `ComposeView` in RecyclerView ViewHolder

**Still XML-based (migration candidates):**
- `FlashcardsActivity` — hero header, action chips, swipe-to-delete (complex Canvas interactions)
- `LearningCheckActivity` / `ExamCheckActivity` — answer input, correct/wrong popups with View animations
- `MainActivity` — main hub with 3 card buttons
- `CategoryActivity` — category list
- `SettingsActivity` / `SettingsFragment` — preferences UI
- Navigation drawer — uses mikepenz MaterialDrawer library (requires library replacement)
- Bottom sheets (`EditCategoryBottomSheet`, CSV import)

**Drawable cleanup completed:** Reduced from 110+ drawable files (plus 10 DPI-variant directories) to 36 actively-referenced drawables. All DPI-variant PNG directories have been removed (redundant with minSdk 31 and vector XML). Remaining drawables are used by screens that still use XML layouts.

**Rule: All new UI must use Jetpack Compose.** When touching an existing XML-based screen, migrate it to Compose if the scope allows. Do not introduce new XML layouts or drawable XML files.

## Localization

Supports English (default) and Polish (`values-pl/`). String resources split across feature-specific files (e.g. `learning_strings.xml`, `category_strings.xml`, `exam_strings.xml`, `chat_strings.xml`).

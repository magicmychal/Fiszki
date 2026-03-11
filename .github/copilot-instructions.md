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
- **Theme-aware colors (dynamic colors)**: Never hardcode colors. The app supports three themes (light, dark, yellow) so every UI element must adapt dynamically. In Compose, use `MaterialTheme.colorScheme.*` (e.g. `MaterialTheme.colorScheme.primary`, `MaterialTheme.colorScheme.onSurface`). In remaining XML, use `?attr/colorPrimary`, `?attr/colorOnSurface`, `?attr/colorSurfaceContainerHigh`, etc. In dialogs, resolve theme attributes dynamically via `TypedValue` + `theme.resolveAttribute()`. When using M3 components like `TimePicker`, `DatePicker`, or `NavigationRail`, always wrap them in `FiszkiTheme` so they inherit the active color scheme — the default M3 baseline is purple and will look wrong under the yellow or dark theme. If a Compose component appears with purple/default colors, it means `FiszkiTheme` is missing or the `ColorScheme` doesn't cover enough tokens (check `ComposeTheme.kt`).
- **Localization**: All user-facing strings must be in `res/values/` (English) and `res/values-pl/` (Polish). In Compose use `stringResource(R.string.…)`, in Activities use `getString(R.string.…)` with format args. Never use hardcoded text.
- **Material Design 3**: Follow M3 guidelines. Use `MaterialAlertDialogBuilder` for new dialogs, M3 components and tokens.
- **Phone and tablet responsive layout**: Every new screen or UI change must work on both phone (<600dp width) and tablet (>=600dp width). On tablet the app uses `NavigationRail` + split-view; on phone it uses `NavigationBar` + single-pane. Use `AdaptiveNavHost` breakpoint (`WIDTH_DP_MEDIUM_LOWER_BOUND` = 600dp) for layout decisions. Constrain content width on tablet with `Modifier.widthIn(max = 500.dp)` for full-screen content like setup/check screens. Use `rememberSaveable` (not `remember`) for any state that must survive Activity recreation when navigating between activities. When embedding Compose in fragments/dialogs, use `DialogFragment` with `ComposeView` in `onCreateView()` — never put `ComposeView` inside a plain `AlertDialog` (it lacks `ViewTreeLifecycleOwner`). Test mentally: "does this work on phone? does this work on tablet side-by-side?"

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

Supports English (default) and Polish (`values-pl/`). String resources split across feature-specific files (e.g. `learning_strings.xml`, `category_strings.xml`, `exam_strings.xml`). User-facing terminology uses "set" (not "category") — internal code identifiers still use "category" for historical reasons.

## Changelog

Maintain `CHANGELOG.md` in the project root using [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) format.

- **Every PR/branch**: Add entries under `## [Unreleased]` describing what was added, changed, fixed, or removed.
- **Every merge to `master`**: Move `[Unreleased]` entries into a new versioned section (`## [X.YZ] - YYYY-MM-DD`) matching the `versionName` in `app/build.gradle`. The `[Unreleased]` heading must remain at the top for future changes.
- Use subsections: `### Added`, `### Changed`, `### Fixed`, `### Removed` (only include subsections that apply).
- Write entries from the user's perspective, not implementation details.

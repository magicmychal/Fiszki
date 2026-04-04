# Fiszki

Fiszki (Polish for "flashcards") is an Android flashcard learning application. It helps users create, organize, and study flashcards with multiple learning modes, notification-based reminders, and exam functionality.

## Features

- **My Words** - Create and manage flashcards organized into sets with language pairs (e.g., English -> Polish)
- **Practice Mode** - Study flashcards by set, language, or all at once with real-time feedback and adaptive spaced repetition
- **Exam Mode** - Test yourself with a configurable number of questions or all cards at once, then review wrong answers
- **Notification Reminders** - Periodic notifications that prompt you to translate a random flashcard (configurable frequency: 1, 5, 15, 30, or 60 minutes)
- **Night Mode** - Dark theme support toggled from the navigation drawer
- **Statistics** - Track pass/fail stats per flashcard with the ability to reset
- **Localization** - Full English and Polish UI support

## How the Learning Algorithm Works

Fiszki uses two learning algorithms that you can toggle between in Settings. Choose the one that works best for your learning style.

### FSRS (Free Spaced Repetition Scheduler) — Primary Algorithm

FSRS is a modern, scientifically-backed spaced repetition algorithm that schedules reviews at optimal intervals to maximize long-term retention with minimal effort.

#### Card States

Every flashcard has one of four states that track your learning progress:

| State | Meaning |
|---|---|
| **New** | Never reviewed. The card has no memory data yet. |
| **Learning** | Being learned for the first time. You'll see it again soon. |
| **Review** | Graduated to long-term memory. Intervals grow with each success. |
| **Relearning** | Previously known but forgotten. Back to short intervals. |

#### How Ratings Work

After each answer in practice mode, FSRS automatically assigns a rating based on your performance:

| Rating | When it happens |
|---|---|
| **Easy** | Correct on 1st attempt, answered within 2 minutes, exact match |
| **Good** | Correct on 1st attempt, but took longer or had minor typos |
| **Hard** | Correct, but only after a retry (wrong answer followed by a correct one) |
| **Again** | Skipped or gave up |

#### Key Concepts

**Stability** measures how long a memory lasts. Higher stability means you can wait longer before the next review. After a successful review, stability increases. After forgetting, it drops.

**Difficulty** (1.0 - 10.0) represents how hard a card is for you personally. Cards you consistently get right become easier. Cards you struggle with become harder. Difficulty affects how fast stability grows.

**Retrievability** is the probability that you can recall a card right now. It starts high after a review and decays over time following a forgetting curve. When retrievability drops to about 90%, it's time to review.

**Interval** is the number of days until the next scheduled review. It's calculated from stability and your desired retention rate (90% by default).

#### Mastery with FSRS

When FSRS is active, the mastery percentage for each set is the **average retrievability** across all cards. This tells you what percentage of the set you could recall right now. It naturally decays over time if you don't review, and increases after practice sessions.

### Legacy Algorithm

The legacy algorithm uses simple priority-based random selection. Each card has a priority level (0-5) that changes based on your answers:
- Correct answer → priority increases (max 5)
- Wrong answer → priority decreases (min 0)

Cards with lower priority appear more often, helping you focus on words you struggle with. Mastery is calculated as your overall pass rate (correct answers / total attempts).

### Exam Mode

Exam mode uses a dedicated algorithm that shuffles all cards in the selected set and presents them one by one without repetition — each card appears at most once per exam session. You choose to test yourself on 5, 10, 15, 25, 50 questions, or **all cards in the set** (default). Unlike practice mode, exams don't use spaced repetition and don't update card memory data — they're pure knowledge tests.

## Architecture

The app follows an Android Activity-based architecture with Jetpack Compose as the primary UI toolkit. New screens are built entirely in Compose, while some legacy screens still use XML layouts (being migrated incrementally).

```
click.quickclicker.fiszki/
├── activity/                    # Activities (screens)
│   ├── MainActivity.kt          # Main hub with 3 cards: My Words, Learning, Exam
│   ├── SplashScreen.kt          # Launcher activity
│   ├── NavHostActivity.kt       # Single-activity architecture with bottom navigation
│   ├── NotificationLaunchActivity.kt # Trampoline for notification launches
│   ├── AboutActivity.kt         # About & credits screen
│   ├── AlgorithmInfoActivity.kt # Algorithm explanation screen
│   ├── SettingsActivity.kt      # Settings (preferences, night mode, diagnostics)
│   ├── CheckActivity.kt         # Quick notification quiz
│   ├── ComposeTheme.kt          # FiszkiTheme — bridges XML themes to Compose MaterialTheme
│   ├── CategoryColors.kt        # Set color definitions
│   ├── AdaptiveNavHost.kt       # Tablet/phone responsive navigation
│   ├── ChangeActivityManager.kt # Navigation helper with transitions
│   ├── exam/                    # Exam flow
│   │   ├── ExamActivity.kt      # Setup screen
│   │   ├── ExamScreen.kt        # Compose UI: exam configuration
│   │   ├── ExamCheckScreen.kt   # Answer checking (Compose)
│   │   ├── ExamCheckActivity.kt # Host for answer checking
│   │   ├── ExamBadAnswerActivity.kt # Wrong answer review
│   │   └── ExamFragment.kt      # Tab fragment for exam in NavHostActivity
│   ├── learning/                # Learning/practice flow
│   │   ├── LearningActivity.kt  # Setup screen
│   │   ├── LearningScreen.kt    # Compose UI: practice configuration
│   │   ├── LearningCheckScreen.kt # Answer checking (Compose)
│   │   ├── LearningCheckActivity.kt # Host for answer checking
│   │   ├── BadAnswerDialog.kt   # Wrong answer retry/skip dialog
│   │   ├── AlgorithmDebugReportScreen.kt # Debug info overlay
│   │   ├── TitleFonts.kt        # Custom font definitions for Compose
│   │   ├── LearningFragment.kt  # Tab fragment for practice in NavHostActivity
│   │   └── LearningActivity.kt  # Legacy learning activity
│   └── myWords/                 # Word & set management
│       ├── CategoryTabScreen.kt # Main set/word view (Compose)
│       ├── CategoryManagerSingleton.kt # Track selected set
│       ├── FlashcardDetailFragment.kt # Detail pane (tablet split-view)
│       ├── category/            # Set management
│       │   ├── CategoryActivity.kt # Set list
│       │   ├── CategoryFragment.kt # Set list fragment
│       │   ├── CategoryShowAdapter.kt # Recycler adapter
│       │   ├── CreateSetActivity.kt # Add new set
│       │   └── EditSetActivity.kt # Edit set details
│       └── flashcards/          # Flashcard management
│           ├── FlashcardsActivity.kt # Flashcard list for a set
│           ├── FlashcardShowAdapter.kt # Recycler with Compose items
│           ├── AddFlashcardActivity.kt # Add flashcard
│           ├── EditFlashcardActivity.kt # Edit flashcard
│           └── SelectedFlashcardsSingleton.kt # Track multi-selected cards
├── algorithm/                   # Flashcard selection & scheduling logic
│   ├── Algorithm.kt             # Card drawing (random selection for legacy mode)
│   ├── Drawer.kt                # Random number utility
│   ├── MultiplierPoints.kt      # Priority weight calculation (legacy)
│   ├── PriorityCount.kt         # Priority distribution counter (legacy)
│   ├── CatcherFlashcardToAlgorithm.kt # Algorithm wrapper
│   ├── fsrs/                    # FSRS (Free Spaced Repetition Scheduler)
│   │   ├── FsrsModels.kt        # FsrsCard, FsrsState, FsrsRating enums
│   │   ├── FsrsScheduler.kt     # FSRS v6 algorithm implementation
│   │   ├── FsrsCardSelector.kt  # Card queue with retry logic
│   │   └── FsrsRatingMapper.kt  # Derive rating from user behavior
│   ├── exam/                    # Exam-specific card selection
│   │   └── ExamCardSelector.kt  # Shuffle + no-repeat logic
│   └── debug/                   # Debug/statistics
│       └── SessionCardRecord.kt # Per-card session data for debug report
├── database/                    # Database layer (Jetpack Room)
│   ├── FiszkiDatabase.kt        # Room database singleton
│   ├── FlashcardDao.kt          # Room DAO: flashcard queries
│   └── CategoryDao.kt           # Room DAO: category/set queries
├── model/                       # Data models
│   ├── category/                # Set (Category) model
│   │   ├── Category.kt          # Room entity
│   │   ├── CategoryRepository.kt # DAO wrapper
│   │   └── ValidationCategory.kt # Input validation
│   └── flashcard/               # Flashcard model
│       ├── Flashcard.kt         # Room entity with FSRS fields
│       ├── FlashcardRepository.kt # DAO wrapper
│       └── ValidationFlashcards.kt # Input validation
├── dialogs/                     # Dialogs & sheets for interactions
│   ├── Flashcard/               # Flashcard CRUD dialogs
│   ├── category/                # Set CRUD dialogs
│   ├── check/                   # Answer result dialogs
│   ├── exam/                    # Exam-related dialogs
│   ├── learning/                # Learning mode dialogs
│   ├── information/             # Info dialogs
│   ├── csv/                     # CSV import/export
│   ├── ReminderScheduleDialog.kt # Notification schedule dialog
│   └── ReminderScheduleDialogFragment.kt # Dialog fragment
├── drawer/                      # Navigation drawer (mikepenz MaterialDrawer)
│   ├── DrawerMain.kt            # Drawer setup
│   └── drawerItem/              # Individual drawer items (night mode, etc.)
├── listeners/                   # Click listeners for flashcard operations
├── settings/                    # Settings utilities
│   └── ChoosenCategoryAdapter.kt # Adapter for set selection
├── ui/                          # UI utilities & Compose helpers
│   ├── BlobShape.kt             # Custom blob shape for animations
│   ├── TabletContentWrapper.kt  # Responsive layout wrapper
│   ├── OrientationHelper.kt     # Portrait locking on phones
│   ├── CategoryFormComponents.kt # Reusable form components
│   └── DiffHighlight.kt         # Diff highlighting for wrong answers
├── AlarmReceiver.kt             # Notification scheduling via AlarmManager
├── Alert.kt                     # Alert dialog builder utilities
├── Checker.kt                   # String comparison utility (exact + relaxed)
├── HapticFeedback.kt            # Vibration feedback (correct/wrong)
├── LocalSharedPreferences.kt    # SharedPreferences wrapper
├── NightModeController.kt       # Theme switching (light/dark/yellow)
├── Rules.kt                     # Flashcard validation rules
└── FiszkiApplication.kt         # Application class
```

## Tech Stack

- **Language**: Kotlin (JVM toolchain Java 11)
- **Min SDK**: 31 (Android 12)
- **Target SDK**: 36 (Android 16)
- **UI**: Jetpack Compose (primary), AndroidX, Material Design 3
- **Theming**: `FiszkiTheme` bridges XML theme attributes to Compose `MaterialTheme` for light/dark/yellow support
- **Database**: SQLite via [Jetpack Room](https://developer.android.com/training/data-storage/room) 2.7.1 (compile-time verified queries)
- **FSRS Algorithm**: FSRS v6 scheduler with 21 trained parameters from [open-spaced-repetition](https://github.com/open-spaced-repetition)
- **Navigation Drawer**: [MaterialDrawer](https://github.com/mikepenz/MaterialDrawer) 8.4.5
- **Navigation**: Material 3 Adaptive (responsive phone/tablet layouts)
- **Markdown Rendering**: [JetBrains Markdown JVM](https://github.com/JetBrains/markdown) 0.7.3 (GFM with tables & links)
- **Crash Reporting**: [Sentry Android SDK](https://github.com/getsentry/sentry-java) 8.33.0 (opt-in)
- **Build**: Gradle with Android Gradle Plugin 9.1.0

## Data Model

### Flashcard
| Field | Description |
|-------|-------------|
| id | Auto-generated primary key |
| word | The word to learn |
| translation | The translation/answer |
| categoryID | Foreign key to set |
| priority | Learning priority (0-5) — legacy algorithm only |
| staticPass | Count of correct answers |
| staticFail | Count of wrong answers |
| fsrsStability | Memory stability (FSRS) |
| fsrsDifficulty | Card difficulty 1.0-10.0 (FSRS) |
| fsrsElapsedDays | Days since last review (FSRS) |
| fsrsScheduledDays | Days until next review (FSRS) |
| fsrsReps | Number of repetitions (FSRS) |
| fsrsLapses | Number of times forgotten (FSRS) |
| fsrsState | Card state: New/Learning/Review/Relearning (FSRS) |
| fsrsLastReview | Timestamp of last review (FSRS) |
| fsrsLastRating | Last rating assigned: Again(1)/Hard(2)/Good(3)/Easy(4) (FSRS) |

### Set (Category)
| Field | Description |
|-------|-------------|
| id | Auto-generated primary key |
| category | Set name |
| langFrom | Source language |
| langOn | Target language |
| isEntryByUser | Whether created by user (vs system) |
| isChosen | Whether selected for notifications |
| color | Visual color identifier |

## Building

```bash
./gradlew assembleDebug
```

Unit tests run automatically before every `assembleDebug` — if any test fails, the build fails immediately.

## Testing

The project has two test suites: **unit tests** (no device needed) and **instrumented tests** (require an emulator or device).

### Unit Tests (12 test classes)

Run with:
```bash
./gradlew testDebugUnitTest
```

| Test class | What it covers |
|---|---|
| `CheckerTest` | Strict and relaxed answer matching |
| `CheckerEditDistanceTest` | Levenshtein distance and diff alignment |
| `FsrsSchedulerTest` | FSRS v6 state transitions, stability, intervals |
| `FsrsRatingMapperTest` | Rating derivation from learning behavior (including retries) |
| `FsrsCardSelectorTest` | Queue logic, retry reinsertion, same-card avoidance |
| `ExamCardSelectorTest` | Shuffle + no-repeat logic for exam mode |
| `FlashcardModelTest` | Apostrophe encoding, priority clamping, stats, FSRS field roundtrip |
| `CategoryModelTest` | Apostrophe encoding/decoding, null handling |
| `AlgorithmTest` | Basic algorithm smoke test |
| `TabletContentWrapperTest` | Responsive layout calculations |
| `TabletSetupScreenTest` | Tablet-specific setup screen behavior |
| `TabletCheckScreenTest` | Tablet-specific answer checking screen behavior |

### Instrumented Tests (3 test classes)

Run on a connected device or emulator:
```bash
./gradlew connectedDebugAndroidTest
```

| Test class | What it covers |
|---|---|
| `DBHelperMigrationTest` | Fresh DB schema validation, FSRS column defaults, basic CRUD |
| `LocalSharedPreferencesTest` | Default preference values, write/read roundtrips |
| `RepositoryTest` | Flashcard and category CRUD, filtering, FSRS field persistence |

## Acknowledgements

Fiszki builds on the following open-source projects and research:

| Project | Use | License |
|---------|-----|---------|
| [FSRS (Free Spaced Repetition Scheduler)](https://github.com/open-spaced-repetition/fsrs-rs) | Spaced repetition algorithm — the FSRS v6 scheduler is ported from the reference Rust implementation | MIT |
| [Jetpack Room](https://developer.android.com/training/data-storage/room) | SQLite ORM with compile-time query verification | Apache 2.0 |
| [MaterialDrawer](https://github.com/mikepenz/MaterialDrawer) | Navigation drawer | Apache 2.0 |
| [Sentry Android SDK](https://github.com/getsentry/sentry-java) | Opt-in crash reporting and diagnostics | MIT |
| [JetBrains Markdown JVM](https://github.com/JetBrains/markdown) | Markdown to HTML with GFM tables & link support | Apache 2.0 |
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | UI toolkit | Apache 2.0 |
| [Google Fonts for Compose](https://developer.android.com/develop/ui/compose/text/fonts#downloadable) | Roboto Flex, Roboto Mono, Roboto Serif, Porter Sans Block | Apache 2.0 / OFL |

The FSRS algorithm is based on the research by Jarrett Ye and the [open-spaced-repetition](https://github.com/open-spaced-repetition) community. Default parameters (w[0..20]) are from the FSRS v6 model trained on anonymised Anki review data.

## License

See [LICENSE](LICENSE) file.

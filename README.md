# Fiszki

Fiszki (Polish for "flashcards") is an Android flashcard learning application. It helps users create, organize, and study flashcards with multiple learning modes, notification-based reminders, and exam functionality.

## Features

- **My Words** - Create and manage flashcards organized into categories with language pairs (e.g., English -> Polish)
- **Learning Mode** - Study flashcards by category, language, or all at once with real-time feedback
- **Exam Mode** - Test yourself with a configurable number of questions and review wrong answers
- **Notification Reminders** - Periodic notifications that prompt you to translate a random flashcard (configurable frequency: 1, 5, 15, 30, or 60 minutes)
- **Night Mode** - Dark theme support toggled from the navigation drawer
- **Statistics** - Track pass/fail stats per flashcard with the ability to reset
- **Tutorial** - Sliding tutorial shown on first launch
- **Localization** - Full English and Polish UI support

## Architecture

The app follows a traditional Android Activity-based architecture:

```
eu.qm.fiszki/
├── activity/                    # Activities (screens)
│   ├── MainActivity.java        # Main hub with 3 cards: My Words, Learning, Exam
│   ├── SplashScreen.java        # Launcher - routes to tutorial or main
│   ├── CheckActivity.java       # Notification-triggered flashcard challenge
│   ├── ChangeActivityManager.java # Navigation helper with transitions
│   ├── exam/                    # Exam flow (ExamActivity, ExamCheckActivity, ExamBadAnswerActivity)
│   ├── learning/                # Learning flow (LearningActivity, LearningCheckActivity)
│   └── myWords/                 # Word management (CategoryActivity, FlashcardsActivity)
├── algorithm/                   # Flashcard selection logic
│   ├── Algorithm.java           # Card drawing (random selection)
│   ├── CatcherFlashcardToAlgorithm.java  # Filters flashcard pools
│   ├── Drawer.java              # Random number utility
│   ├── MultiplierPoints.java    # Priority weight calculation
│   └── PriorityCount.java       # Priority distribution counter
├── database/ORM/                # Database layer
│   ├── DBHelper.java            # ORMLite database helper (SQLite)
│   └── DBConfigUtility.java     # ORM configuration
├── dialogs/                     # Material dialogs for all interactions
│   ├── category/                # Add/edit category dialogs
│   ├── check/                   # Pass/fail/empty notification dialogs
│   ├── exam/                    # Exam end, settings, range dialogs
│   ├── flashcard/               # Add/edit/transform/statistic flashcard dialogs
│   └── learning/                # Learning mode selection dialogs
├── drawer/                      # Navigation drawer
│   ├── DrawerMain.java          # Drawer setup
│   └── drawerItem/              # Individual drawer items (notifications, night mode, etc.)
├── listeners/                   # Click listeners for flashcard operations
├── model/                       # Data models
│   ├── category/                # Category model, repository, validation
│   └── flashcard/               # Flashcard model, repository, validation
├── tutorial/                    # Sliding tutorial pages
├── AlarmReceiver.java           # Notification scheduling via AlarmManager
├── Alert.java                   # Alert dialog builder utilities
├── Checker.java                 # String comparison utility
├── LocalSharedPreferences.java  # Notification preferences wrapper
├── NightModeController.java     # Theme switching (light/dark)
├── Rules.java                   # Flashcard validation rules
└── ShowCategoryAdapter.java     # Category list adapter
```

## Tech Stack

- **Language**: Java 11
- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 35 (Android 15)
- **Database**: SQLite via [ORMLite](https://ormlite.com/)
- **UI**: AndroidX, Material Design 3, Material Components
- **Navigation Drawer**: [MaterialDrawer](https://github.com/mikepenz/MaterialDrawer) 8.4.5
- **Dialogs**: [Material Dialogs](https://github.com/afollestad/material-dialogs) 0.9.6.0
- **Spinner**: [Material Spinner](https://github.com/jaredrummler/MaterialSpinner) 1.3.1
- **Tutorial**: [Cleveroad SlidingTutorial](https://github.com/nickseven/SlidingTutorial) 1.0.6
- **Build**: Gradle with Android Gradle Plugin 9.1.0

## Data Model

### Flashcard
| Field | Description |
|-------|-------------|
| id | Auto-generated primary key |
| word | The word to learn |
| translation | The translation/answer |
| categoryID | Foreign key to category |
| priority | Learning priority (0-5), increases on correct answers |
| staticPass | Count of correct answers |
| staticFail | Count of wrong answers |

### Category
| Field | Description |
|-------|-------------|
| id | Auto-generated primary key |
| category | Category name |
| langFrom | Source language |
| langOn | Target language |
| entryByUser | Whether created by user (vs system) |
| choosen | Whether selected for notifications |

## Building

```bash
./gradlew assembleDebug
```

## Running Tests

```bash
./gradlew test
```

## License

See [LICENSE](LICENSE) file.

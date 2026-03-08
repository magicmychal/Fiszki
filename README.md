# Fiszki

Fiszki (Polish for "flashcards") is an Android flashcard learning application. It helps users create, organize, and study flashcards with multiple learning modes, notification-based reminders, and exam functionality.

## Features

- **My Words** - Create and manage flashcards organized into sets with language pairs (e.g., English -> Polish)
- **Learning Mode** - Study flashcards by set, language, or all at once with real-time feedback
- **Exam Mode** - Test yourself with a configurable number of questions and review wrong answers
- **Notification Reminders** - Periodic notifications that prompt you to translate a random flashcard (configurable frequency: 1, 5, 15, 30, or 60 minutes)
- **Night Mode** - Dark theme support toggled from the navigation drawer
- **Statistics** - Track pass/fail stats per flashcard with the ability to reset
- **Tutorial** - Sliding tutorial shown on first launch
- **Localization** - Full English and Polish UI support

## How the Learning Algorithm Works

Fiszki uses a practice system designed to help you remember flashcards through **active recall** and **adaptive repetition**. Here's how it works across all learning modes.

### Priority System

Every flashcard has an internal **priority level** ranging from **0 to 5**. This number reflects how well you know the card:

| Priority | Meaning |
|----------|---------|
| 0 | Brand-new or difficult — you've been getting it wrong |
| 1–2 | Still learning — you've had a few correct answers |
| 3–4 | Getting comfortable — you're answering correctly more often |
| 5 | Well-known — you've answered correctly many times in a row |

- When you **answer correctly**, the card's priority goes **up by 1** (max 5).
- When you **answer incorrectly**, the card's priority goes **down by 1** (min 0).

This means cards you struggle with stay at a low priority, while cards you've mastered gradually climb to the top. The system keeps track of this automatically — you don't need to do anything.

### How Cards Are Picked

During a practice or exam session, the app picks flashcards **randomly** from your selected pool (a specific set or all sets). Every card in the pool has an equal chance of appearing, so you can't predict what comes next. This keeps you on your toes and prevents you from memorising the order rather than the actual words.

### Strict Mode vs. Relaxed Mode

Before starting a practice session, you can choose between two answer-checking modes:

- **Strict mode (on by default)** — Your answer must match the translation **exactly**, including uppercase/lowercase letters and punctuation. This is ideal when precision matters (e.g., learning spelling).
- **Relaxed mode** — The check ignores upper/lowercase differences and trailing dots. For example, "Hello" and "hello." would both be accepted. This is useful for casual practice when you care about knowing the word rather than exact formatting.

### What Happens When You Answer

**Correct answer:**
- A "Correct!" animation appears on screen
- You feel a short, light vibration as confirmation
- The flashcard's priority increases by 1
- Your correct-answer counter for that card goes up
- The next card is drawn automatically

**Wrong answer:**
- A dialog shows you what you typed vs. the correct answer, with differences highlighted in colour
- You feel a longer, stronger vibration so you notice the mistake
- The flashcard's priority decreases by 1
- Your wrong-answer counter for that card goes up
- In learning mode, you can tap "Skip" to move on or "OK" to try a different card
- In exam mode, you proceed to the next question automatically

### Learning Mode

Learning mode is an **open-ended practice session** — there's no fixed number of questions. You keep practising for as long as you want and can finish or skip at any time. Before starting, you choose:

1. **Set** — practise cards from a single set or all sets at once
2. **Language direction** — if a set has language pairs (e.g., English → Polish), you can reverse the direction to practise both ways
3. **Strict/relaxed mode** — how strictly answers are checked

A live status card shows your current correct and total counts so you can track your accuracy during the session.

### Exam Mode

Exam mode is a **fixed-length test**. You choose the number of questions (5, 10, 15, 25, or 50) before starting, along with the same set, direction, and strictness options as learning mode. The differences are:

- There is **no skip button** — you must answer every question
- If you try to leave early, the app asks you to confirm
- At the end, you see a **summary screen** showing your score, the number of correct and wrong answers, and a detailed list of every mistake you made with the correct translations

This makes exam mode ideal for testing yourself before a real test or measuring your progress over time.

### Notification Reminders

You can enable periodic notifications that pop up a random flashcard from your selected sets. When you tap the notification, you're taken to a quick single-card quiz. Your answer still updates the card's priority and statistics, so even a few seconds of practice throughout the day contributes to your learning.

### Statistics

Every flashcard tracks how many times you've answered it correctly and incorrectly. You can view these stats per card and reset them whenever you want (e.g., when you want a fresh start). This helps you identify which words are giving you the most trouble.

### Tips for Effective Learning

- **Practise regularly in short sessions** — multiple 5-minute sessions per day are more effective than one long session per week.
- **Use notification reminders** — quick, random quizzes throughout the day reinforce your memory without effort.
- **Practise in both directions** — if you're learning English → Polish, also try Polish → English to strengthen recall from both sides.
- **Use strict mode for important exams** — it trains you to spell words correctly, not just recognise them.
- **Check your statistics** — if a card has a high fail count, consider reviewing it separately or adding related cards to build context.

## Architecture

The app follows an Android Activity-based architecture with Jetpack Compose as the primary UI toolkit. New screens are built entirely in Compose, while some legacy screens still use XML layouts (being migrated incrementally).

```
eu.qm.fiszki/
├── activity/                    # Activities (screens)
│   ├── MainActivity.kt          # Main hub with 3 cards: My Words, Learning, Exam
│   ├── SplashScreen.kt          # Launcher - routes to tutorial or main
│   ├── CheckActivity.kt         # Notification-triggered flashcard challenge
│   ├── ComposeTheme.kt          # FiszkiTheme — bridges XML themes to Compose MaterialTheme
│   ├── CategoryColors.kt        # Set color definitions
│   ├── ChangeActivityManager.kt # Navigation helper with transitions
│   ├── exam/                    # Exam flow
│   │   ├── ExamActivity.kt      # Setup screen (ComposeView with ExamSetupScreen)
│   │   ├── ExamScreen.kt        # Compose UI: exam configuration
│   │   └── ExamCheckActivity.kt # Answer checking (XML-based)
│   ├── learning/                # Learning flow
│   │   ├── LearningActivity.kt  # Setup screen (ComposeView with PracticeSetupScreen)
│   │   ├── LearningScreen.kt    # Compose UI: practice configuration
│   │   ├── TitleFonts.kt        # Custom font definitions for Compose
│   │   └── LearningCheckActivity.kt # Answer checking (XML-based)
│   └── myWords/                 # Word management
│       ├── category/            # CategoryActivity — set list (XML-based)
│       └── flashcards/          # FlashcardsActivity — flashcard list (XML + Compose items)
├── algorithm/                   # Flashcard selection logic
│   ├── Algorithm.kt             # Card drawing (random selection)
│   ├── Drawer.kt                # Random number utility
│   ├── MultiplierPoints.kt      # Priority weight calculation
│   └── PriorityCount.kt        # Priority distribution counter
├── database/ORM/                # Database layer
│   ├── DBHelper.kt              # ORMLite database helper (SQLite)
│   └── DBConfigUtility.kt       # ORM configuration
├── dialogs/                     # Material dialogs for all interactions
│   ├── category/                # Add/edit set dialogs
│   ├── check/                   # Pass/fail/empty notification dialogs
│   ├── exam/                    # Exam end, settings, range dialogs
│   ├── flashcard/               # Add/edit/transform/statistic flashcard dialogs
│   └── learning/                # Learning mode selection dialogs
├── drawer/                      # Navigation drawer (mikepenz MaterialDrawer)
│   ├── DrawerMain.kt            # Drawer setup
│   └── drawerItem/              # Individual drawer items (notifications, night mode, etc.)
├── listeners/                   # Click listeners for flashcard operations
├── model/                       # Data models
│   ├── category/                # Set model, repository, validation
│   └── flashcard/               # Flashcard model, repository, validation
├── tutorial/                    # Sliding tutorial pages
├── AlarmReceiver.kt             # Notification scheduling via AlarmManager
├── Alert.kt                     # Alert dialog builder utilities
├── Checker.kt                   # String comparison utility
├── HapticFeedback.kt            # Vibration feedback for correct/wrong answers
├── LocalSharedPreferences.kt    # Notification preferences wrapper
├── NightModeController.kt       # Theme switching (light/dark/yellow)
└── Rules.kt                     # Flashcard validation rules
```

## Tech Stack

- **Language**: Kotlin (JVM toolchain Java 11)
- **Min SDK**: 31 (Android 12)
- **Target SDK**: 35 (Android 15)
- **UI**: Jetpack Compose (primary), AndroidX, Material Design 3
- **Theming**: `FiszkiTheme` bridges XML theme attributes to Compose `MaterialTheme` for light/dark/yellow support
- **Database**: SQLite via [ORMLite](https://ormlite.com/) 5.7
- **Navigation Drawer**: [MaterialDrawer](https://github.com/mikepenz/MaterialDrawer) 8.4.5
- **Dialogs**: [Material Dialogs](https://github.com/afollestad/material-dialogs) 0.9.6.0 (Jetifier-converted)
- **Tutorial**: [Cleveroad SlidingTutorial](https://github.com/nickseven/SlidingTutorial) 1.0.6
- **Build**: Gradle with Android Gradle Plugin 9.1.0

## Data Model

### Flashcard
| Field | Description |
|-------|-------------|
| id | Auto-generated primary key |
| word | The word to learn |
| translation | The translation/answer |
| categoryID | Foreign key to set |
| priority | Learning priority (0-5), increases on correct answers |
| staticPass | Count of correct answers |
| staticFail | Count of wrong answers |

### Set (Category)
| Field | Description |
|-------|-------------|
| id | Auto-generated primary key |
| category | Set name |
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

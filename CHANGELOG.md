# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Added
- Combined reminder schedule dialog with modern time picker and day-of-week toggles in a single view

### Changed
- Practice and exam setup screens constrained to 500dp max width on tablet instead of stretching full width
- Practice and exam check screens use the same 500dp max width for consistent sizing across all screens
- Settings toolbar hidden on tablet where the navigation rail already provides context
- Night mode toggle uses MaterialSwitch matching all other toggles on the settings page
- Settings toolbar follows light/dark theme (surface color) instead of always using primary color
- "Correct!" popup restored to original organic blob shape instead of plain circle
- Navigation rail icons centered vertically on tablet
- All theme colors (including time picker) now follow the active light/dark/yellow theme

### Fixed
- Selected set now persists when navigating to practice and back on tablet
- Set detail pane on tablet no longer goes blank after returning from another screen
- Flashcard list refreshes immediately after adding a new flashcard on tablet
- Edit set bottom sheet now slides up correctly on tablet split-view
- Set color and name changes reflect immediately without needing to switch sets
- Content no longer overlaps the status bar on tablet
- Add flashcard dialog no longer jumps down after opening

## [2.2.1] - 2026-03-10

### Added
- Tablet support: NavigationRail on devices with 600dp+ width, bottom navigation kept on phones
- Tablet support: split-view on the categories tab (set list + flashcards side by side)
- Tablet support: 9:16 centered content wrapper for practice and exam check screens on wide displays
- Runtime orientation helper that locks portrait on phones while allowing sensor rotation on tablets
- Unit tests run automatically on every debug build — build fails on test failure
- Unit tests for FsrsCardSelector, Flashcard model, and Category model
- Instrumented tests for DB migrations, SharedPreferences defaults, and repository CRUD
- FSRS (Free Spaced Repetition Scheduler) v6 algorithm for intelligent card scheduling based on memory strength
- "Spaced repetition (FSRS)" toggle in Settings to switch between smart scheduling and legacy random draw
- Automatic rating derivation from learning behavior (speed, accuracy, attempt count) — no manual self-rating needed
- FSRS state indicator (4 colored dots) on flashcard list items when spaced repetition is enabled
- Daily reminder notification now opens the full practice screen instead of the old single-word quiz
- Opt-in diagnostic data reporting via Sentry (disabled by default)
- "Send diagnostic data" toggle in Settings with test crash button
- Device info sent to Sentry on opt-in (OS, model, resolution, languages)
- Fastlane metadata for F-Droid submission (EN + PL descriptions, icon, changelog)
- INTERNET and ACCESS_NETWORK_STATE permissions (for diagnostics only)

### Changed
- Package renamed from eu.qm.fiszki to click.quickclicker.fiszki
- LearningCheckActivity rewritten in Jetpack Compose (from XML layout)
- ExamCheckActivity rewritten in Jetpack Compose (from XML layout)
- ExamBadAnswerActivity wrapped with tablet content wrapper
- NavHostActivity rewritten in Compose with adaptive navigation (NavigationRail/BottomNav)
- Screen orientation now controlled at runtime instead of manifest declarations
- Sentry auto-init disabled; initialization is manual and opt-in only

### Fixed
- Fixed crash on fresh install caused by R8 obfuscating DBHelper constructor (ProGuard rules updated)

### Removed
- Bugfender SDK (replaced by Sentry)

## [2.00] - 2026-03-01

### Added
- Redesigned learning and exam setup screens with Jetpack Compose
- CSV export for flashcard sets
- Haptic feedback for correct/wrong answers

### Changed
- Renamed "category" to "set" throughout the UI
- Improved scoring system

### Fixed
- Various bug fixes and stability improvements

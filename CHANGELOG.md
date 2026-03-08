# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Added
- Opt-in diagnostic data reporting via Sentry (disabled by default)
- "Send diagnostic data" toggle in Settings with test crash button
- Device info sent to Sentry on opt-in (OS, model, resolution, languages)
- Fastlane metadata for F-Droid submission (EN + PL descriptions, icon, changelog)
- INTERNET and ACCESS_NETWORK_STATE permissions (for diagnostics only)

### Changed
- Sentry auto-init disabled; initialization is manual and opt-in only

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

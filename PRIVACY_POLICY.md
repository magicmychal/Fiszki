# Privacy Policy

**Fiszki** — Flashcard Learning App
**Effective date:** 2026-03-10
**Developer:** Michal Pawlicki

---

## Overview

Fiszki is a flashcard app that helps you learn vocabulary. Your privacy matters — the app is designed to work fully offline and does not collect any personal data unless you explicitly opt in.

## Data stored on your device

All flashcards, sets, learning progress, and app preferences (theme, notification schedule, algorithm choice) are stored locally on your device in an SQLite database and SharedPreferences. This data never leaves your device unless you choose to export it yourself (e.g. CSV export via the share sheet).

## Data collection — opt-in diagnostics only

By default, Fiszki **does not collect, transmit, or share any data**.

If you enable the **"Send diagnostic data"** toggle in Settings, the app initializes [Sentry](https://sentry.io) to collect crash reports and basic device information, including:

- Crash stack traces and error messages
- Android version and API level
- Device manufacturer and model
- Screen resolution
- Device language and app language

This data is sent to Sentry's servers (hosted in the EU — `ingest.de.sentry.io`) and is used solely to identify and fix bugs. No personal identifiers, flashcard content, or usage analytics are collected — even when diagnostics are enabled.

You can disable diagnostic data at any time in Settings. When disabled, Sentry is not initialized and no data is transmitted.

## Permissions

The app requests the following Android permissions:

| Permission | Purpose |
|---|---|
| `INTERNET` | Required only for diagnostic data (when opt-in is enabled) and downloading Google Fonts |
| `ACCESS_NETWORK_STATE` | Checks connectivity before attempting network operations |
| `POST_NOTIFICATIONS` | Sends local study reminder notifications (user-configured) |
| `SCHEDULE_EXACT_ALARM` | Schedules study reminder notifications at precise times |
| `RECEIVE_BOOT_COMPLETED` | Re-schedules notifications after device restart |
| `VIBRATE` | Provides haptic feedback on correct/wrong answers |

None of these permissions are used to collect or transmit personal data.

## Third-party libraries

Fiszki uses the following third-party dependencies:

| Library | Purpose | Privacy relevance |
|---|---|---|
| [Sentry Android SDK](https://sentry.io/privacy/) | Crash reporting and diagnostics | Receives crash and device data **only when opt-in is enabled**. Data processed in the EU. See [Sentry's privacy policy](https://sentry.io/privacy/). |
| [Google Fonts (via Compose)](https://developers.google.com/fonts) | Downloads font files (Roboto Flex, Roboto Mono, Roboto Serif, Porter Sans Block) | Font requests are made to Google servers. See [Google's privacy policy](https://policies.google.com/privacy). No user data is sent — only standard HTTP requests for font files. |
| [ORMLite](https://ormlite.com/) | Local SQLite database ORM | Local only, no network access |
| [AndroidX / Jetpack Compose](https://developer.android.com/jetpack) | UI framework and Android components | Local only, no network access |
| [Material Components / Material Design 3](https://m3.material.io/) | UI components and theming | Local only, no network access |
| [MaterialDrawer (mikepenz)](https://github.com/mikepenz/MaterialDrawer) | Navigation drawer UI | Local only, no network access |
| [material-dialogs (afollestad)](https://github.com/afollestad/material-dialogs) | Dialog UI components | Local only, no network access |

## Children's privacy

Fiszki does not knowingly collect any personal information from children. The app does not require an account, does not serve ads, and does not track users.

## Data retention

- **On-device data** is retained until you uninstall the app or clear its data.
- **Diagnostic data** (if opt-in is enabled) is retained according to [Sentry's data retention policy](https://sentry.io/privacy/).

## Changes to this policy

If this privacy policy is updated, the changes will be reflected in this document with an updated effective date.

## Contact

If you have questions about this privacy policy, you can reach the developer via the contact information listed on the app's Google Play Store page.

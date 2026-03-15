# About Fiszki

## A Word from the Author/Maintainer

Fiszki is a revival of the original Fiszki app, which I co-created with my best friend during high school as part of the RST Newbies team. After graduation, life took us in different directions, and we no longer had time to maintain or develop the app further.

Now, living in a new country and needing to learn the local language, I found myself missing the tool I once relied on. While there are many language-learning apps available—both free and paid—I needed something specific: an app that would force me to actively write words and allow me to create my own custom flashcard sets.

The original app was no longer compatible with the latest Android versions and had been removed from the Play Store. Thanks to advancements in AI, I was able to quickly revive the application. You can find the original version, updated to work on Android 13 and above, in my GitHub repository. The version you’re using now is a redesign to better align with current style guidelines.

**Note:** I’m neither a developer nor a designer, so there may be bugs or limitations. To stay true to the original spirit, I’ve kept the app open-source. Feel free to report issues, contribute to the project, or help with translations via the official GitHub repository.

---

## How It Works

Fiszki doesn’t come with pre-made flashcard sets. Instead, it’s designed for users who already have their own learning materials—whether from language courses or other apps. The app helps you turn those materials into flashcards and memorize them effectively.

### Key Features:
- **Create Custom Flashcard Sets:** Build your own sets of vocabulary or concepts.
- **Practice Mode:** Test yourself using the FSRS (Free Spaced Repetition Scheduler) algorithm, which optimizes card repetition to help you remember words faster. [Learn more about FSRS here.]
- **Exam Mode:** Challenge yourself with a stricter mode where you can’t retry words immediately. Results are shown only after completing a set number of cards.

---

## Additional Settings

### Import and Export Sets
- **Import:** Easily import flashcard sets in CSV format directly from the "Edit Set" screen. Prepare your sets in Excel or Google Sheets on your computer, then import them into the app for seamless management.
- **Export:** Share your sets with others by exporting them from the app.

## Acknowledgements
Fiszki builds on the following open-source projects and research:

|Project|Use|License|
|---|---|---|
|[FSRS (Free Spaced Repetition Scheduler)](https://github.com/open-spaced-repetition/fsrs-rs)|Spaced repetition algorithm — the FSRS v6 scheduler is ported from the reference Rust implementation|MIT|
|[ORMLite](https://ormlite.com/)|SQLite ORM for Android|ISC|
|[MaterialDrawer](https://github.com/mikepenz/MaterialDrawer)|Navigation drawer|Apache 2.0|
|[Material Dialogs](https://github.com/afollestad/material-dialogs)|Dialog framework|MIT|
|[SlidingTutorial](https://github.com/nickseven/SlidingTutorial)|First-launch tutorial|MIT|
|[Sentry Android SDK](https://github.com/getsentry/sentry-java)|Opt-in crash reporting and diagnostics|MIT|
|[Jetpack Compose](https://developer.android.com/jetpack/compose)|UI toolkit|Apache 2.0|
|[Google Fonts for Compose](https://developer.android.com/develop/ui/compose/text/fonts#downloadable)|Roboto Flex, Roboto Mono, Roboto Serif, Porter Sans Block|Apache 2.0 / OFL|

The FSRS algorithm is based on the research by Jarrett Ye and the [open-spaced-repetition](https://github.com/open-spaced-repetition) community. Default parameters (w[0..20]) are from the FSRS v6 model trained on anonymised Anki review data.
package click.quickclicker.fiszki.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

/**
 * Transparent trampoline activity launched from the daily reminder notification.
 * Loads all flashcards and immediately starts LearningCheckActivity, then finishes.
 */
class NotificationLaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OrientationHelper.lockPortraitOnPhone(this)

        val flashcards = FlashcardRepository(this).getAllFlashcards()

        if (flashcards.isEmpty()) {
            Toast.makeText(this, R.string.settings_choose_category_empty, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        ChangeActivityManager(this).goToLearningCheck(flashcards)
        finish()
    }
}

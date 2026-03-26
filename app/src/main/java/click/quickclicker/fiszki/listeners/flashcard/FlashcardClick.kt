package click.quickclicker.fiszki.listeners.flashcard

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import click.quickclicker.fiszki.activity.myWords.flashcards.EditFlashcardActivity
import click.quickclicker.fiszki.model.flashcard.Flashcard

class FlashcardClick(
    private val activity: AppCompatActivity,
    private val flashcard: Flashcard
) : View.OnClickListener {

    private var clickCount = 0

    override fun onClick(view: View) {
        clickCount++
        val handler = Handler(Looper.getMainLooper())
        val r = Runnable { clickCount = 0 }

        if (clickCount == 1) {
            // Single click
            handler.postDelayed(r, 250)
        } else if (clickCount == 2) {
            // Double click
            clickCount = 0
            activity.startActivity(
                Intent(activity, EditFlashcardActivity::class.java)
                    .putExtra(EditFlashcardActivity.EXTRA_FLASHCARD_ID, flashcard.id)
            )
        }
    }
}

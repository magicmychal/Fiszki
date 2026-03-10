package click.quickclicker.fiszki.listeners.flashcard

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import click.quickclicker.fiszki.dialogs.flashcard.EditAndDeleteFlashcardDialog
import click.quickclicker.fiszki.model.flashcard.Flashcard

class FlashcardClick(
    private val activity: Activity,
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
            EditAndDeleteFlashcardDialog(activity, flashcard).show()
        }
    }
}

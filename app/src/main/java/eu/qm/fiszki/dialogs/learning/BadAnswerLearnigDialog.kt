package eu.qm.fiszki.dialogs.learning

import android.content.Context
import android.text.Html
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.learning.LearningCheckActivity
import eu.qm.fiszki.model.flashcard.Flashcard

class BadAnswerLearnigDialog(
    context: Context,
    flashcard: Flashcard,
    lca: LearningCheckActivity,
    correctAnswer: String = flashcard.getTranslation()
) : MaterialAlertDialogBuilder(context) {

    init {
        setTitle(R.string.alert_title_fail)
        setMessage(
            Html.fromHtml(
                "${context.resources.getString(R.string.learning_check_dialog_bad_answer_1)} " +
                    "<b>${correctAnswer}</b><br>" +
                    context.resources.getString(R.string.learning_check_dialog_bad_answer_2),
                Html.FROM_HTML_MODE_LEGACY
            )
        )
        setCancelable(false)
        setPositiveButton(R.string.button_action_ok) { dialog, _ ->
            dialog.dismiss()
        }
        setNeutralButton(R.string.learning_check_dialog_skip_btn) { dialog, _ ->
            lca.drawFlashcard()
            dialog.dismiss()
        }
    }
}

package eu.qm.fiszki.dialogs.learning

import android.content.Context
import android.text.Html
import com.afollestad.materialdialogs.MaterialDialog
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.learning.LearningCheckActivity
import eu.qm.fiszki.model.flashcard.Flashcard

class BadAnswerLearnigDialog(
    context: Context,
    flashcard: Flashcard,
    lca: LearningCheckActivity
) : MaterialDialog.Builder(context) {

    init {
        title(R.string.alert_title_fail)
        content(
            Html.fromHtml(
                "${context.resources.getString(R.string.learning_check_dialog_bad_answer_1)} " +
                    "<b>${flashcard.getTranslation()}</b><br>" +
                    context.resources.getString(R.string.learning_check_dialog_bad_answer_2)
            )
        )

        positiveText(R.string.button_action_ok)
        positiveColor(context.resources.getColor(R.color.ColorPrimaryDark))

        neutralText(R.string.learning_check_dialog_skip_btn)
        neutralColor(context.resources.getColor(R.color.ColorPrimaryDark))

        autoDismiss(false)
        onPositive(okClick())
        onNeutral(skipClick(lca))
    }

    private fun okClick(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            dialog.dismiss()
        }
    }

    private fun skipClick(lca: LearningCheckActivity): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            lca.drawFlashcard()
            dialog.dismiss()
        }
    }
}

package eu.qm.fiszki.dialogs.exam

import android.app.Activity
import android.text.Html
import com.afollestad.materialdialogs.MaterialDialog
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager

class EndExamDialog(
    private val mActivity: Activity,
    private val mBadAnswer: ArrayList<*>,
    private val mGoodAnswer: ArrayList<*>
) : MaterialDialog.Builder(mActivity) {

    init {
        title(R.string.exam_check_end_dialog_title)
        cancelable(false)
        content(Html.fromHtml(setContent()))
        onPositive(exitExamCheck())
        onNeutral(goToExamBadAnswer())
        positiveColor(mActivity.resources.getColor(R.color.ColorPrimaryDark))

        if (mBadAnswer.size >= 1) {
            positiveText(R.string.button_action_ok)
            neutralColor(mActivity.resources.getColor(R.color.md_red_A700))
            neutralText(R.string.exam_check_end_dialog_bad_answer_btn)
        } else {
            positiveText(R.string.exam_check_end_dialog_hurra_btn)
        }
    }

    private fun setContent(): String {
        val percentage = (mGoodAnswer.size * 100.0f / (mGoodAnswer.size + mBadAnswer.size)).toInt()
        return "<b>${mActivity.resources.getString(R.string.exam_check_end_dialog_content_1)}</b> ${mGoodAnswer.size}<br>" +
            "<b>${mActivity.resources.getString(R.string.exam_check_end_dialog_content_2)}</b> ${mBadAnswer.size}<br>" +
            "<b>${mActivity.resources.getString(R.string.exam_check_end_dialog_content_3)}</b> $percentage%"
    }

    private fun goToExamBadAnswer(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { _, _ ->
            ChangeActivityManager(mActivity).goToExamBadAnswer(mBadAnswer)
        }
    }

    private fun exitExamCheck(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { _, _ ->
            ChangeActivityManager(mActivity).exitExamCheck()
        }
    }
}

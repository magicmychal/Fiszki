package eu.qm.fiszki.dialogs.exam

import android.app.Activity
import android.text.Html
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager

class EndExamDialog(
    private val mActivity: Activity,
    private val mBadAnswer: ArrayList<*>,
    private val mGoodAnswer: ArrayList<*>
) : MaterialAlertDialogBuilder(mActivity) {

    init {
        setTitle(R.string.exam_check_end_dialog_title)
        setCancelable(false)
        setMessage(Html.fromHtml(setContent(), Html.FROM_HTML_MODE_LEGACY))
        setPositiveButton(
            if (mBadAnswer.isNotEmpty()) R.string.button_action_ok else R.string.exam_check_end_dialog_hurra_btn
        ) { _, _ ->
            ChangeActivityManager(mActivity).exitExamCheck()
        }
        if (mBadAnswer.isNotEmpty()) {
            setNeutralButton(R.string.exam_check_end_dialog_bad_answer_btn) { _, _ ->
                ChangeActivityManager(mActivity).goToExamBadAnswer(mBadAnswer)
            }
        }
    }

    private fun setContent(): String {
        val percentage = (mGoodAnswer.size * 100.0f / (mGoodAnswer.size + mBadAnswer.size)).toInt()
        return "<b>${mActivity.resources.getString(R.string.exam_check_end_dialog_content_1)}</b> ${mGoodAnswer.size}<br>" +
            "<b>${mActivity.resources.getString(R.string.exam_check_end_dialog_content_2)}</b> ${mBadAnswer.size}<br>" +
            "<b>${mActivity.resources.getString(R.string.exam_check_end_dialog_content_3)}</b> $percentage%"
    }
}

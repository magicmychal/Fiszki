package click.quickclicker.fiszki.dialogs.exam

import android.app.Activity
import android.text.Html
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.ChangeActivityManager
import click.quickclicker.fiszki.model.flashcard.Flashcard

class EndExamDialog(
    private val mActivity: Activity,
    private val mSummaryData: ExamSummaryData,
    private val mGoodAnswer: ArrayList<Flashcard>
) : MaterialAlertDialogBuilder(mActivity) {

    init {
        setTitle(R.string.exam_check_end_dialog_title)
        setCancelable(false)
        setMessage(Html.fromHtml(setContent(), Html.FROM_HTML_MODE_LEGACY))
        setPositiveButton(
            if (mSummaryData.incorrectCount > 0) R.string.button_action_ok else R.string.exam_check_end_dialog_hurra_btn
        ) { _, _ ->
            if (mSummaryData.incorrectCount > 0) {
                ChangeActivityManager(mActivity).goToExamSummary(mSummaryData)
            } else {
                ChangeActivityManager(mActivity).exitExamCheck()
            }
        }
        if (mSummaryData.incorrectCount == 0) {
            setNeutralButton(R.string.exam_check_end_dialog_hurra_btn) { _, _ ->
                ChangeActivityManager(mActivity).exitExamCheck()
            }
        }
    }

    private fun setContent(): String {
        val percentage = (mGoodAnswer.size * 100.0f / (mGoodAnswer.size + mSummaryData.incorrectCount)).toInt()
        return "<b>${mActivity.resources.getString(R.string.exam_check_end_dialog_content_1)}</b> ${mGoodAnswer.size}<br>" +
            "<b>${mActivity.resources.getString(R.string.exam_check_end_dialog_content_2)}</b> ${mSummaryData.incorrectCount}<br>" +
            "<b>${mActivity.resources.getString(R.string.exam_check_end_dialog_content_3)}</b> $percentage%"
    }
}

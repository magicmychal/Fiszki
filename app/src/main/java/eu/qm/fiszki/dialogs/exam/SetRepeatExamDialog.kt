package eu.qm.fiszki.dialogs.exam

import android.app.Activity
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.qm.fiszki.R

class SetRepeatExamDialog(mActivity: Activity) : MaterialAlertDialogBuilder(mActivity) {

    private var mCardsText: TextView
    private var mRange: ArrayList<Int>

    init {
        setTitle(R.string.exam_repeat_dialog_title)

        mCardsText = mActivity.findViewById(R.id.exam_repeat_text) as TextView
        mRange = arrayListOf(5, 10, 15, 25, 50)

        val items = mRange.map { it.toString() }.toTypedArray()
        setSingleChoiceItems(items, -1) { _, which ->
            mCardsText.text = mRange[which].toString()
        }
    }
}

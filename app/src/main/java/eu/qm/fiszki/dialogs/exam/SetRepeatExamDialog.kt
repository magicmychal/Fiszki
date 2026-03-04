package eu.qm.fiszki.dialogs.exam

import android.app.Activity
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import eu.qm.fiszki.R

class SetRepeatExamDialog(private val mActivity: Activity) : MaterialDialog.Builder(mActivity) {

    private var mCardsText: TextView
    private var mRange: ArrayList<Int>

    init {
        title(R.string.exam_repeat_dialog_title)

        mCardsText = mActivity.findViewById(R.id.exam_repeat_text) as TextView
        mRange = ArrayList()

        items(getRepeatValues())
        itemsCallbackSingleChoice(-1, onClickCategory())
    }

    private fun getRepeatValues(): ArrayList<Int> {
        mRange.apply {
            add(5)
            add(10)
            add(15)
            add(25)
            add(50)
        }
        return mRange
    }

    private fun onClickCategory(): MaterialDialog.ListCallbackSingleChoice {
        return MaterialDialog.ListCallbackSingleChoice { _, _, which, _ ->
            mCardsText.text = mRange[which].toString()
            true
        }
    }
}

package click.quickclicker.fiszki.dialogs.check

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import click.quickclicker.fiszki.R
import java.util.Random

class PassCheckDialog(private val mActivity: Activity) : MaterialAlertDialogBuilder(mActivity) {
    init {
        setTitle(R.string.alert_title_pass)
        setMessage(randMessage())
        setCancelable(false)
        setPositiveButton(R.string.button_action_ok) { dialog, _ ->
            dialog.dismiss()
            mActivity.finish()
        }
    }

    private fun randMessage(): String {
        return when (Random().nextInt(10)) {
            0 -> context.resources.getString(R.string.statistic_0)
            1 -> context.resources.getString(R.string.statistic_1)
            2 -> context.resources.getString(R.string.statistic_2)
            3 -> context.resources.getString(R.string.statistic_3)
            4 -> context.resources.getString(R.string.statistic_4)
            5 -> context.resources.getString(R.string.statistic_5)
            6 -> context.resources.getString(R.string.statistic_6)
            7 -> context.resources.getString(R.string.statistic_7)
            8 -> context.resources.getString(R.string.statistic_8)
            9 -> context.resources.getString(R.string.statistic_9)
            else -> ""
        }
    }
}

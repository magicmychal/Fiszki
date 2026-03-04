package eu.qm.fiszki.dialogs.information

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import eu.qm.fiszki.R

class InformationFlashcardDialog(context: Context) : MaterialDialog.Builder(context) {
    init {
        title(R.string.button_action_info)
        // icon(ContextCompat.getDrawable(context, R.drawable.ic_info_black_24dp))
        content(R.string.info_flashcard)
        positiveText(R.string.button_action_ok)
        positiveColor(context.resources.getColor(R.color.ColorPrimaryDark))
    }
}

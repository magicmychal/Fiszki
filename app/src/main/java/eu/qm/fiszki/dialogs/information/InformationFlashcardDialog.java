package eu.qm.fiszki.dialogs.information;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;

import eu.qm.fiszki.R;

/**
 * Created by mBoiler on 20.11.2016.
 */

public class InformationFlashcardDialog extends MaterialDialog.Builder {

    public InformationFlashcardDialog(@NonNull Context context) {
        super(context);
        this.title(R.string.button_action_info);
       // this.icon(ContextCompat.getDrawable(context,R.drawable.ic_info_black_24dp));
        this.content(R.string.info_flashcard);
        this.positiveText(R.string.button_action_ok);
        this.positiveColor(context.getResources().getColor(R.color.ColorPrimaryDark));
    }
}

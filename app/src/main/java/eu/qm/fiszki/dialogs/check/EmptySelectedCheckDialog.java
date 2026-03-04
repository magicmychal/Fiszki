package eu.qm.fiszki.dialogs.check;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import eu.qm.fiszki.R;

/**
 * Created by mBoiler on 20.12.2016.
 */

public class EmptySelectedCheckDialog extends MaterialDialog.Builder {

    private Activity mActivity;

    public EmptySelectedCheckDialog(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;

        this.title(R.string.alert_title_fail);
        this.content(R.string.check_dialog_empty_selected);

        this.positiveText(R.string.button_action_ok);
        this.positiveColor(context.getResources().getColor(R.color.ColorPrimaryDark));
    }


}


package eu.qm.fiszki.dialogs.check;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.Html;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import eu.qm.fiszki.FirebaseManager;
import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.learning.LearningCheckActivity;
import eu.qm.fiszki.model.flashcard.Flashcard;

/**
 * Created by mBoiler on 20.12.2016.
 */

public class EmptyDBCheckDialog extends MaterialDialog.Builder {

    private Activity mActivity;

    public EmptyDBCheckDialog(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;

        this.title(R.string.alert_title_fail);
        this.content(R.string.check_dialog_emptyDB);

        this.positiveText(R.string.button_action_ok);
        this.positiveColor(context.getResources().getColor(R.color.ColorPrimaryDark));
        this.autoDismiss(false);
        this.onPositive(okClick());

    }

    private MaterialDialog.SingleButtonCallback okClick() {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                mActivity.finish();
            }
        };
    }
}


package eu.qm.fiszki.dialogs.check;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Random;

import eu.qm.fiszki.R;

/**
 * Created by mBoiler on 20.12.2016.
 */

public class PassCheckDialog extends MaterialDialog.Builder {

    private Activity mActivity;

    public PassCheckDialog(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;

        this.title(R.string.alert_title_pass);
        this.content(randMessage());

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

    private String randMessage(){
        int rand = new Random().nextInt(10);
        switch(rand){
            case 0 : return context.getResources().getString(R.string.statistic_0);
            case 1 : return context.getResources().getString(R.string.statistic_1);
            case 2 : return context.getResources().getString(R.string.statistic_2);
            case 3 : return context.getResources().getString(R.string.statistic_3);
            case 4 : return context.getResources().getString(R.string.statistic_4);
            case 5 : return context.getResources().getString(R.string.statistic_5);
            case 6 : return context.getResources().getString(R.string.statistic_6);
            case 7 : return context.getResources().getString(R.string.statistic_7);
            case 8 : return context.getResources().getString(R.string.statistic_8);
            case 9 : return context.getResources().getString(R.string.statistic_9);
        }
        return "";
    }
}


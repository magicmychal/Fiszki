package eu.qm.fiszki.dialogs.flashcard;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.text.Html;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import eu.qm.fiszki.R;
import eu.qm.fiszki.model.flashcard.Flashcard;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

/**
 * Created by mBoiler on 22.12.2016.
 */

public class StatisticFlashcardDialog extends MaterialDialog.Builder {

    public StatisticFlashcardDialog(@NonNull Activity activity, @NonNull Flashcard flashcard) {
        super(activity);
        this.title(R.string.flashcard_statistic_dialog_title);
        this.content(Html.fromHtml(setContent(activity, flashcard)));

        this.autoDismiss(false);

        this.positiveColor(activity.getResources().getColor(R.color.ColorPrimaryDark));
        this.positiveText(R.string.button_action_ok);
        this.onPositive(okClick());

        this.neutralColor(activity.getResources().getColor(R.color.md_red_A700));
        this.neutralText(R.string.flashcard_statistic_dialog_reset);
        this.onNeutral(resetClick(activity,flashcard));
    }

    private String setContent(Activity activity, Flashcard flashcard) {
        return activity.getResources().getString(R.string.flashcard_statistic_dialog_pass) +
                " " +
                "<font color='#63d471'>" +
                flashcard.getStaticPass() +
                "</font>" +
                "<br>" +
                activity.getResources().getString(R.string.flashcard_statistic_dialog_fail) +
                " " +
                "<font color='#d7263d'>" +
                flashcard.getStaticFail() +
                "</font>";
    }

    private MaterialDialog.SingleButtonCallback resetClick(final Activity activity,
                                                           final Flashcard flashcard) {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new FlashcardRepository(activity).resetFlashcardStatistic(flashcard);
                dialog.dismiss();
            }
        };
    }

    private MaterialDialog.SingleButtonCallback okClick() {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        };
    }
}

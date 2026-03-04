package eu.qm.fiszki.dialogs.exam;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.text.Html;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import eu.qm.fiszki.FirebaseManager;
import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.ChangeActivityManager;

/**
 * Created by mBoiler on 26.11.2016.
 */

public class EndExamDialog extends MaterialDialog.Builder {

    private Activity mActivity;
    private ArrayList mBadAnswer;
    private ArrayList mGoodAnswer;

    public EndExamDialog(@NonNull Activity activity, ArrayList badAnswer, ArrayList goodAnswer) {
        super(activity);
        this.mActivity = activity;
        this.mBadAnswer = badAnswer;
        this.mGoodAnswer = goodAnswer;
        this.title(R.string.exam_check_end_dialog_title);
        this.cancelable(false);
        this.content(Html.fromHtml(setContent()));
        this.onPositive(exitExamCheck());
        this.onNeutral(goToExamBadAnswer());
        this.positiveColor(activity.getResources().getColor(R.color.ColorPrimaryDark));

        if(mBadAnswer.size()>=1) {
            this.positiveText(R.string.button_action_ok);
            this.neutralColor(activity.getResources().getColor(R.color.md_red_A700));
            this.neutralText(R.string.exam_check_end_dialog_bad_answer_btn);
        }else{
            this.positiveText(R.string.exam_check_end_dialog_hurra_btn);
        }
    }

    private String setContent() {

        return "<b>" +
                mActivity.getResources().getString(R.string.exam_check_end_dialog_content_1) +
                "</b>" +
                " "+
                mGoodAnswer.size()+
                "<br>"+
                "<b>"+
                mActivity.getResources().getString(R.string.exam_check_end_dialog_content_2) +
                "</b>"+
                " "+
                mBadAnswer.size()+
                "<br>"+
                "<b>"+
                mActivity.getResources().getString(R.string.exam_check_end_dialog_content_3) +
                "</b>"+
                " "+
                (int) ((mGoodAnswer.size() * 100.0f) / (mGoodAnswer.size()+mBadAnswer.size()))+
                "%";
    }

    private MaterialDialog.SingleButtonCallback goToExamBadAnswer() {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new FirebaseManager(mActivity).sendEvent(FirebaseManager.Params.EXAM_SHOW_BAD);
                new ChangeActivityManager(mActivity).goToExamBadAnswer(mBadAnswer);
            }
        };
    }

    private MaterialDialog.SingleButtonCallback exitExamCheck() {
        return new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new FirebaseManager(mActivity).sendEvent(FirebaseManager.Params.EXAM_OK_BTN);
                new ChangeActivityManager(mActivity).exitExamCheck();
            }
        };
    }


}

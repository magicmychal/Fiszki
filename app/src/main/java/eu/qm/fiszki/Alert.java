package eu.qm.fiszki;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import eu.qm.fiszki.activity.MainActivity;

public class Alert {
    public void pass(final Context context, String message, String title,
                     String nameButton) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(nameButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) context).finish();

            }
        });
        alertDialog.show();
    }

    public void fail(final Context context, String orginalWord, String message,String messageCorrectIs,
                     String messeageAgain,String title,String nameButton) {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(Html.fromHtml(message + " "+ messageCorrectIs + " " + "<b>\"" + orginalWord + "\"</b>" + ". " +
                "\n" + messeageAgain));
        alertDialog.setCancelable(false);
        alertDialog.setButton(nameButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    public void buildAlert(String title, String message, String buttonText, Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }
    public void learningModePass(final Context context, String message, String title,
                                 String nameButton) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(nameButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }
    public void learningModeFail(final Context context, String orginalWord, String message,
                                 String title, String nameButton) {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(Html.fromHtml(message + " " + "<b>" + orginalWord + "</b>"));
        alertDialog.setButton(nameButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }
    public void emptyBase(final Context context,String message, String title,String nameButton){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(Html.fromHtml(message));
        alertDialog.setButton(nameButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
        alertDialog.show();
    }
    public void deleteRecord(final Context context,String message, String title,String nameButton,
                             String nameButton2) {
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(Html.fromHtml(message));
        alertDialog.setButton(nameButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setButton2(nameButton2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public MaterialDialog.Builder addFiszkiToFeature(Activity activity){
        return new MaterialDialog.Builder(activity)
                .title(activity.getResources().getString(R.string.alert_no_category_title))
                .content(activity.getResources().getString(R.string.alert_no_category_messege))
                .positiveText( activity.getResources().getString(R.string.button_action_ok))
                .contentGravity(GravityEnum.START);
    }
}

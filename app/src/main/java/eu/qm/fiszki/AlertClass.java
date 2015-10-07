package eu.qm.fiszki;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

//// TODO: 2015-10-03 Dadanie cofnięcia do MainActivity po zamknięciu allertDialog. 
public class AlertClass {

    String OriginalWord;
    Context Window;
    String Message;

    public void Pass(Context activActivity, String message) {
        Message = message;
        Window = activActivity;
        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(Window).create();
        alertDialog.setTitle("Gratulacje");
        alertDialog.setMessage(Message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            }
        });
        alertDialog.show();
    }

    public void Fail(Context activActivity,String OrginalWord,String message) {
        Message = message;
        OriginalWord = OrginalWord;
        Window = activActivity;
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(Window).create();
        alertDialog.setTitle("Niestety");
        alertDialog.setMessage(Message + " " + OriginalWord);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();

    }


}

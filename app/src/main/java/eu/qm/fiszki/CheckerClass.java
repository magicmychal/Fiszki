package eu.qm.fiszki;

import android.widget.EditText;

public class CheckerClass {

    EditText EnteredWord;
    String OriginalWord;

    public Boolean Check(String originalWord,EditText enteredWord){
        EnteredWord = enteredWord;
        OriginalWord = originalWord;

        if(OriginalWord.compareTo(EnteredWord.getText().toString())== 0) return true;
        else return false;
    }
}

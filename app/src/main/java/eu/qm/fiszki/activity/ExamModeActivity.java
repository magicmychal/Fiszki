package eu.qm.fiszki.activity;

import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormatSymbols;

import eu.qm.fiszki.Alert;
import eu.qm.fiszki.Checker;
import eu.qm.fiszki.R;
import eu.qm.fiszki.database.DBAdapter;
import eu.qm.fiszki.database.DBModel;
import eu.qm.fiszki.database.DBStatus;

public class ExamModeActivity extends AppCompatActivity {

    TextView word;
    EditText enteredWord;
    DBAdapter myDb = new DBAdapter(this);
    DBStatus OpenDataBase = new DBStatus();
    TextView numberOfTrue;
    TextView numberOfFalse;
    TextView numberOfProcent;
    TextView numberOfTotal;
    TextView subtitle;
    boolean firstAnswer=true;
    int numberOfRepeat;
    int repeat=0;
    int trueAnswer=0;
    int falseAnswer=0;
    String wordFromData;
    String expectedWord;
    Checker check;
    Alert message;
    Context context;
    Cursor c;
    Button repeate;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        choosePacked();
        context = this;
        message = new Alert();
        check = new Checker();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_exam_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_OK) {
            if (check.Check(expectedWord, enteredWord.getText().toString())) {
                trueAnswer++;
                repeat++;
                algorith();
            } else {
                falseAnswer++;
                repeat++;
                algorith();
            }

        } else if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void keyboardAction() {
        enteredWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (check.Check(expectedWord, enteredWord.getText().toString())) {
                        trueAnswer++;
                        repeat++;
                        algorith();
                    } else {
                        falseAnswer++;
                        repeat++;
                        algorith();
                    }
                }
                    return false;
            }
        });
    }



    public void algorith(){

        if(repeat!=numberOfRepeat) {
            enteredWord.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(enteredWord,0);
                }
            },0);
            c = myDb.getAllRows();
            enteredWord.setText("");
            enteredWord.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            word.setText("");
            int cCount = c.getCount();
            int cPosition = myDb.intRowValue(DBModel.SETTINGS_NAME, "cursorPosition");
            if (cPosition < cCount) {
                c.move(cPosition);
                cPosition++;
                myDb.updateRow("cursorPosition", cPosition);
            } else {
                cPosition = 1;
                myDb.updateRow("cursorPosition", cPosition);
            }

            wordFromData = c.getString(c.getColumnIndex(DBModel.KEY_WORD));
            word.append(wordFromData);
            expectedWord = c.getString(c.getColumnIndex(DBModel.KEY_TRANSLATION));
            enteredWord.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        } else {
            enteredWord.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(enteredWord.getWindowToken(), 0);
                }
            }, 0);
            setContentView(R.layout.learning_mode_statistic_layout);
            menu.findItem(R.id.action_OK).setVisible(false);
            numberOfFalse = (TextView) findViewById(R.id.numberOfFalse);
            numberOfTrue = (TextView) findViewById(R.id.numberOfTrue);
            numberOfProcent = (TextView) findViewById(R.id.numberOfProcent);
            numberOfTotal = (TextView) findViewById(R.id.numberOfTotal);
            numberOfFalse.setText(Integer.toString(falseAnswer));
            numberOfTrue.setText(Integer.toString(trueAnswer));
            int percent = (int)((trueAnswer * 100.0f) / numberOfRepeat);
            numberOfProcent.setText(Integer.toString(percent)+"%");
            numberOfTotal.setText(Integer.toString(numberOfRepeat));
            repeate = (Button) findViewById(R.id.repeate);
            repeate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                    Intent myIntent = new Intent(ExamModeActivity.this, ExamModeActivity.class);
                    startActivity(myIntent);
                }
            });
            subtitle = (TextView) findViewById(R.id.statistic_subtitle);
            if(percent<=100 && percent>=95){
                subtitle.setText(R.string.statistic_fantastic_answer);
            }
            if(percent<=94 && percent>=80){
                subtitle.setText(R.string.statistic_nice_answer);
            }
            if(percent<=79 && percent>=50){
                subtitle.setText(R.string.statistic_good);
            }
            if(percent<=49 && percent>=30){
                subtitle.setText(R.string.statistic_barely_answer);
            }
            if(percent<=30 && percent>=0){
                subtitle.setText(R.string.statistic_needwork_answer);
            }
        }
    }

    public void choosePacked(){
        CharSequence[] items = {"10", "20", "50"};
        new AlertDialog.Builder(ExamModeActivity.this)
                .setCancelable(false)
                .setSingleChoiceItems(items, 0, null)
                .setTitle(R.string.exam_mode_repeat_number)
                .setPositiveButton(R.string.button_action_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selected = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if (selected == 0) { //10
                            numberOfRepeat=10;
                        }
                        if (selected == 1) { //20
                            numberOfRepeat=20;
                        }
                        if (selected == 2) { //50
                            numberOfRepeat=50;
                        }
                        setContentView(R.layout.activity_check);
                        enteredWord = (EditText) findViewById(R.id.EnteredWord);
                        word = (TextView) findViewById(R.id.textView3);
                        OpenDataBase.openDB(myDb);
                        algorith();
                        keyboardAction();
                    }
                })
                .setNegativeButton(R.string.button_action_back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .show();
    }
}

package eu.qm.fiszki.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import eu.qm.fiszki.Alert;
import eu.qm.fiszki.Checker;
import eu.qm.fiszki.R;
import eu.qm.fiszki.database.DBAdapter;
import eu.qm.fiszki.database.DBModel;
import eu.qm.fiszki.database.DBStatus;

public class LearningModeActivity extends AppCompatActivity {

    TextView word;
    EditText enteredWord;
    DBAdapter myDb = new DBAdapter(this);
    DBStatus OpenDataBase = new DBStatus();

    String wordFromData;
    String expectedWord;
    Checker check;
    Alert message;
    Context context;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);


        OpenDataBase.openDB(myDb);
        Cursor c = myDb.getAllRows();

        int cCount = c.getCount();
        int cPosition = myDb.intRowValue(DBModel.SETTINGS_NAME, "cursorPosition");
        if(cPosition < cCount) {
            c.move(cPosition);
            cPosition++;
            myDb.updateRow("cursorPosition", cPosition);
        } else {
            cPosition = 1;
            myDb.updateRow("cursorPosition", cPosition);
        }

        wordFromData = c.getString(c.getColumnIndex(DBModel.KEY_WORD));
        expectedWord = c.getString(c.getColumnIndex(DBModel.KEY_TRANSLATION));
        enteredWord = (EditText) findViewById(R.id.EnteredWord);
        enteredWord.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        enteredWord.setText("");
        word = (TextView) findViewById(R.id.textView3);
        word.append(wordFromData);
        enteredWord.requestFocus();
        check = new Checker();
        message = new Alert();
        context = this;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        algorith();
        keyboardAction();
    }


    @Override
    public void onResume() {
        super.onResume();
        enteredWord.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exam_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_OK) {
            if (check.Check(expectedWord, enteredWord.getText().toString())) {
                finish();
                startActivity(getIntent());
            } else {
                enteredWord.setText("");
                message.fail(this, expectedWord, getString(R.string.alert_message_fail),getString(R.string.alert_message_tryagain) ,getString(R.string.alert_title_fail), getString(R.string.button_action_ok));

            }
        } else if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void keyboardAction(){
        enteredWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){

                    if (check.Check(expectedWord, enteredWord.getText().toString())) {
                        algorith();
                    } else {
                        enteredWord.setText("");
                        message.fail(LearningModeActivity.this, expectedWord, getString(R.string.alert_message_fail), getString(R.string.alert_message_tryagain), getString(R.string.alert_title_fail), getString(R.string.button_action_ok));
                    }

                    enteredWord.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            keyboard.showSoftInput(enteredWord, 0);
                        }
                    },50);

                }

                return false;
            }
        });
    }
    public void algorith(){

            enteredWord.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(enteredWord, 0);
                }
            }, 0);
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
        }
}
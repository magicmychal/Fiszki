package eu.qm.fiszki.activity;

import android.content.Context;
import android.app.ActionBar;
import android.app.Dialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Random;
import eu.qm.fiszki.Alert;
import eu.qm.fiszki.Checker;
import eu.qm.fiszki.database.DBAdapter;
import eu.qm.fiszki.database.DBModel;
import eu.qm.fiszki.database.DBStatus;
import eu.qm.fiszki.R;

public class CheckActivity extends AppCompatActivity {

    TextView word;
    EditText enteredWord;
    DBAdapter myDb = new DBAdapter(this);
    DBStatus OpenDataBase = new DBStatus();
    Alert alert;
    Context context;

    String wordFromData;
    String expectedWord;
    int rowId;
    int rowPriority;
    boolean firstTry = true;

    MenuItem mi;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        OpenDataBase.openDB(myDb);
        Cursor c = myDb.getAllRows();
        alert = new Alert();
        context = this;

        if (myDb.getAllRows().getCount() <= 0) {
            alert.emptyBase(context, getString(R.string.main_activity_empty_base_main_layout), getString(R.string.alert_title_fail), getString(R.string.button_action_ok));

        } else {

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

            if (myDb.getAllRows().getCount() > 0) {
                wordFromData = c.getString(c.getColumnIndex(DBModel.KEY_WORD));
                expectedWord = c.getString(c.getColumnIndex(DBModel.KEY_TRANSLATION));
                rowId = c.getInt(c.getColumnIndex(DBModel.KEY_ROWID));
                rowPriority = c.getInt(c.getColumnIndex(DBModel.KEY_PRIORITY));
                enteredWord = (EditText) findViewById(R.id.EnteredWord);
                enteredWord.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                enteredWord.setText("");
                word = (TextView) findViewById(R.id.textView3);
                word.append(wordFromData);
                enteredWord.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                keyboardAction();
            } else {
                alert.emptyBase(this, getString(R.string.main_activity_empty_base_main_layout), getString(R.string.alert_title_fail), getString(R.string.button_action_ok));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(myDb.getAllRows().getCount()>0) {
            enteredWord.setText("");
        }
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OpenDataBase.closeDB(myDb);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        mi = item;
        id = R.id.action_OK;
        Alert message = new Alert();
        Checker check = new Checker();
        if (id == R.id.action_OK)
        {
           if(check.Check(expectedWord, enteredWord.getText().toString()))
           {
               message.pass(this, getString(R.string.alert_message_pass), getString(R.string.alert_title_pass), getString(R.string.button_action_ok));

               if(rowPriority<5 && firstTry) {
                   myDb.updateFlashcardPriority(rowId, rowPriority + 1);
               }
           }
           else
           {
               enteredWord.setText("");
               enteredWord.requestFocus();
            message.fail(this, expectedWord, getString(R.string.alert_message_fail),
                    getString(R.string.alert_message_tryagain), getString(R.string.alert_title_fail), getString(R.string.button_action_ok));
               firstTry = false;

             myDb.updateFlashcardPriority(rowId, 1);
           }
        }
        else if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void keyboardAction(){
        enteredWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                   id = R.id.action_OK;
                    onOptionsItemSelected(mi);
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

    private Cursor drawCardAlgorithm() {
        final int[] points = {25, 20, 15, 10, 5};
        int[] totalPoints = new int[5];
        int[] section = new int[5];
        int drawn = 0;

        Cursor cursorPriority = null;

        for(int i=0; i<5; i++) {
            Cursor cardsPriority = myDb.getAllRowsPriority(i+1);
            int count = cardsPriority.getCount();
            totalPoints[i] = count * points[i];
            if(i <= 0) {
                section[i] = totalPoints[i];
            }else {
                section[i] = totalPoints[i] + section[i-1];
            }
        }
        Random rand = new Random();
        drawn = rand.nextInt(section[4]);
        drawn += 1;

        if(drawn <= section[0]) {
            cursorPriority = myDb.getRandomRowWithpriority(1);
        } else if(drawn <= section[1]) {
            cursorPriority = myDb.getRandomRowWithpriority(2);
        } else if(drawn <= section[2]) {
            cursorPriority = myDb.getRandomRowWithpriority(3);
        } else if(drawn <= section[3]) {
            cursorPriority = myDb.getRandomRowWithpriority(4);
        } else if(drawn <= section[4]+1) {
            cursorPriority = myDb.getRandomRowWithpriority(5);
        }

        return cursorPriority;
    }
}

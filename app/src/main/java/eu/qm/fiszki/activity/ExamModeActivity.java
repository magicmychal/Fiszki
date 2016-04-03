package eu.qm.fiszki.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.qm.fiszki.Alert;
import eu.qm.fiszki.Algorithm;
import eu.qm.fiszki.ChoosenCategoryAdapter;
import eu.qm.fiszki.R;
import eu.qm.fiszki.Rules;
import eu.qm.fiszki.database.DBAdapter;
import eu.qm.fiszki.database.DBStatus;
import eu.qm.fiszki.model.Category;
import eu.qm.fiszki.model.CategoryManagement;
import eu.qm.fiszki.model.Flashcard;
import eu.qm.fiszki.model.FlashcardManagement;


public class ExamModeActivity extends AppCompatActivity {

    public Context context;
    FlashcardManagement flashcardManagement;
    Algorithm algorithm;
    TextView word;
    EditText enteredWord;
    DBAdapter myDb = new DBAdapter(this);
    DBStatus OpenDataBase = new DBStatus();
    TextView numberOfTrue;
    TextView numberOfFalse;
    TextView numberOfProcent;
    TextView numberOfTotal;
    TextView subtitle;
    boolean firstAnswer = true;
    int numberOfRepeat;
    int repeat = 0;
    int trueAnswer = 0;
    int falseAnswer = 0;
    String wordFromData;
    String expectedWord;
    Rules rules;
    Alert message;
    Cursor c;
    Button repeate;
    Menu menu;
    int position = 0;
    Flashcard flashcard;
    RadioGroup radioGroup;
    Button button;
    AlertDialog dialog;
    ArrayAdapter<String> dataAdapter;
    List<String> items;
    ArrayAdapter arrayadapter;
    private Spinner spinner;
    private CategoryManagement categoryManagement;
    private EditText categoryName;
    private Button addCategoryButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_exam_mode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        categoryManagement = new CategoryManagement(context);

        context = this;

        message = new Alert();
        rules = new Rules();
        algorithm = new Algorithm(this);
        flashcardManagement = new FlashcardManagement(this);
        radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton) {
                    numberOfRepeat = 10;
                }
                if (checkedId == R.id.radioButton2) {
                    numberOfRepeat = 20;
                }
                if (checkedId == R.id.radioButton3) {
                    numberOfRepeat = 50;
                }
                if (checkedId == R.id.radioButton4) {
                    numberOfRepeat = -1;
                }
            }
        });
        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.radioButton) {
                    runActivityCheck();
                }
                if (selectedId == R.id.radioButton2) {
                    runActivityCheck();
                }
                if (selectedId == R.id.radioButton3) {
                    runActivityCheck();
                }
                if (selectedId == R.id.radioButton4) {
                    runActivityCheck();
                }
            }
        });

        ListView listView = (ListView)findViewById(R.id.listView);
        ChoosenCategoryAdapter choosenCategoryAdapter = new ChoosenCategoryAdapter(context, R.layout.layout_adapter, categoryManagement.getUserCategory());
        listView.setAdapter(choosenCategoryAdapter);


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
            if (rules.check(expectedWord, enteredWord.getText().toString())) {
                trueAnswer++;
                repeat++;
                newDraw();
            } else {
                falseAnswer++;
                repeat++;
                newDraw();
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
                    if (rules.check(expectedWord, enteredWord.getText().toString())) {
                        trueAnswer++;
                        repeat++;
                        newDraw();
                    } else {
                        falseAnswer++;
                        repeat++;
                        newDraw();
                    }
                }
                return false;
            }
        });
    }


    public void newDraw() {

        if (repeat != numberOfRepeat) {
            enteredWord.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(enteredWord, 0);
                }
            }, 0);
            enteredWord.setText("");
            enteredWord.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            word.setText("");

            flashcard = algorithm.drawCardAlgorithm();

            wordFromData = flashcard.getWord();
            word.append(wordFromData);
            expectedWord = flashcard.getTranslation();
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
            int percent = (int) ((trueAnswer * 100.0f) / numberOfRepeat);
            numberOfProcent.setText(Integer.toString(percent) + "%");
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
            if (percent <= 100 && percent >= 95) {
                subtitle.setText(R.string.statistic_fantastic_answer);
            }
            if (percent <= 94 && percent >= 80) {
                subtitle.setText(R.string.statistic_nice_answer);
            }
            if (percent <= 79 && percent >= 50) {
                subtitle.setText(R.string.statistic_good);
            }
            if (percent <= 49 && percent >= 30) {
                subtitle.setText(R.string.statistic_barely_answer);
            }
            if (percent <= 30 && percent >= 0) {
                subtitle.setText(R.string.statistic_needwork_answer);
            }
        }
    }

    public void runActivityCheck() {
        setContentView(R.layout.activity_check);
        enteredWord = (EditText) findViewById(R.id.EnteredWord);
        word = (TextView) findViewById(R.id.textView3);
        OpenDataBase.openDB(myDb);
        newDraw();
        keyboardAction();
    }
}

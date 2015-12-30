package eu.qm.fiszki.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import eu.qm.fiszki.AlarmReceiver;
import eu.qm.fiszki.Alert;
import eu.qm.fiszki.R;
import eu.qm.fiszki.database.DBAdapter;
import eu.qm.fiszki.database.DBModel;
import eu.qm.fiszki.database.DBStatus;


public class AddWordActivity extends AppCompatActivity {

    EditText inputWord, inputTranslation;
    DBAdapter myDb = new DBAdapter(this);
    DBStatus OpenDataBase = new DBStatus();
    SettingsActivity settings = new SettingsActivity();
    Alert alert = new Alert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        inputWord = (EditText) findViewById(R.id.inputWord);
        inputWord.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        inputWord.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        inputTranslation = (EditText) findViewById(R.id.inputTranslation);
        inputTranslation.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        OpenDataBase.openDB(myDb);
        settings.alarmIntent = new Intent(this, AlarmReceiver.class);
        settings.pendingIntent = PendingIntent.getBroadcast(this, 0, settings.alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        settings.manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        settings.alarm = new AlarmReceiver();
        settings.context = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        clickDone();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addword, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_new_word) {
            if (inputWord.getText().toString().isEmpty() || inputTranslation.getText().toString().isEmpty()) {
                alert.buildAlert(getString(R.string.alert_title), getString(R.string.alert_message_onEmptyFields), getString(R.string.button_action_ok), AddWordActivity.this);
            } else if (myDb.getRowValue(DBModel.KEY_WORD, inputWord.getText().toString()) == true) {
                alert.buildAlert(getString(R.string.alert_title), getString(R.string.alert_message_onRecordExist), getString(R.string.button_action_ok), AddWordActivity.this);
                inputWord.setText(null);
                inputTranslation.setText(null);
                inputWord.requestFocus();

            } else if (!TextUtils.isEmpty(inputWord.getText().toString()) &&
                    !TextUtils.isEmpty(inputTranslation.getText().toString())) {
                myDb.insertRow(inputWord.getText().toString(), inputTranslation.getText().toString(),1);
                Toast.makeText(AddWordActivity.this,
                        getString(R.string.add_new_word_toast), Toast.LENGTH_SHORT).show();
                inputWord.setText(null);
                inputTranslation.setText(null);
                inputWord.requestFocus();
                if (myDb.getAllRows().getCount() == 1) {
                    settings.alarm.start(settings.manager, settings.context, settings.pendingIntent, settings.time);
                    myDb.updateRow(settings.notificationPosition, 3);
                    myDb.updateRow(settings.notificationStatus,1);
                    alert.buildAlert(
                            this.getString(R.string.alert_title_pass),
                            this.getString(R.string.alert_add_first_word_message),
                            this.getString(R.string.button_action_ok),
                            this);
                }
            }
        }
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickDone(){
    inputTranslation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                if (inputWord.getText().toString().isEmpty() || inputTranslation.getText().toString().isEmpty()) {
                    alert.buildAlert(getString(R.string.alert_title), getString(R.string.alert_message_onEmptyFields), getString(R.string.button_action_ok), AddWordActivity.this);
                } else if (myDb.getRowValue(DBModel.KEY_WORD, inputWord.getText().toString())) {
                    alert.buildAlert(getString(R.string.alert_title), getString(R.string.alert_message_onRecordExist), getString(R.string.button_action_ok), AddWordActivity.this);
                    inputWord.setText(null);
                    inputTranslation.setText(null);
                    inputWord.requestFocus();

                } else if (!TextUtils.isEmpty(inputWord.getText().toString()) &&
                        !TextUtils.isEmpty(inputTranslation.getText().toString())) {
                    myDb.insertRow(inputWord.getText().toString(), inputTranslation.getText().toString(),1);
                    Toast.makeText(AddWordActivity.this,
                            getString(R.string.add_new_word_toast), Toast.LENGTH_SHORT).show();
                    inputWord.setText(null);
                    inputTranslation.setText(null);
                    inputWord.requestFocus();
                    if (myDb.getAllRows().getCount() == 1) {
                        settings.alarm.start(settings.manager, settings.context, settings.pendingIntent, settings.time);
                        myDb.updateRow(settings.notificationPosition, 3);
                        myDb.updateRow(settings.notificationStatus, 1);
                        alert.buildAlert(
                                getString(R.string.alert_title_pass),
                                getString(R.string.alert_add_first_word_message),
                                getString(R.string.button_action_ok),
                                AddWordActivity.this);
                    }
                }
            }

            return true;
        }
    });
}

}
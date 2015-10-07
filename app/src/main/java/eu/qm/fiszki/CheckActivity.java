package eu.qm.fiszki;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

//// TODO: 2015-10-03 Dodanie akceptacji słowa Enterem 
public class CheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return true;
    }
    //Do "WordFromData" wprowadzi sie dane z bazy danych.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String WordFromData = "Gall";
        EditText EnteredWord = (EditText)findViewById(R.id.EnteredWord);
        CheckerClass investigator = new CheckerClass();
        AlertClass message = new AlertClass();

        if (id == R.id.action_OK) {
           if(investigator.Check(WordFromData,EnteredWord)){
                message.Pass(this,getString(R.string.alert_pass));
           }
           else{
                message.Fail(this,WordFromData,getString(R.string.alert_fail));
           }
        }
        return super.onOptionsItemSelected(item);
    }
}

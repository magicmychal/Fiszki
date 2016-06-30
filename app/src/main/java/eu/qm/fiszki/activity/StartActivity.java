package eu.qm.fiszki.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import eu.qm.fiszki.R;

public class StartActivity extends AppCompatActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SharedPreferences runCheck = getSharedPreferences("hasRunBefore", 0);
                Boolean hasRun = runCheck.getBoolean("hasRun", false);
                if (!hasRun) {
                    SharedPreferences settings = getSharedPreferences("hasRunBefore", 0);
                    SharedPreferences.Editor edit = settings.edit();
                    edit.putBoolean("hasRun", true);
                    edit.commit();
                    Intent tutorial = new Intent(StartActivity.this, TutorialActivity.class);
                    startActivity(tutorial);
                    finish();
                }else{
                    Intent tutorial = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(tutorial);
                    finish();
                }
            }
        },3000);

    }
}
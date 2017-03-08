package eu.qm.fiszki.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.fabric.sdk.android.Fabric;
import java.util.Timer;

import eu.qm.fiszki.R;
import eu.qm.fiszki.tutorial.TutorialActivity;

public class SplashScreen extends AppCompatActivity {

    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mTimer = new Timer();

        SharedPreferences runCheck = getSharedPreferences("hasRunBefore", 0);
        Boolean hasRun = runCheck.getBoolean("hasRun", false);
        if (!hasRun) {
            SharedPreferences settings = getSharedPreferences("hasRunBefore", 0);
            SharedPreferences.Editor edit = settings.edit();
            edit.putBoolean("hasRun", true);
            edit.commit();
            Intent tutorial = new Intent(SplashScreen.this, TutorialActivity.class);
            startActivity(tutorial);
            finish();
        } else {
            Intent tutorial = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(tutorial);
            finish();
        }
    }
}

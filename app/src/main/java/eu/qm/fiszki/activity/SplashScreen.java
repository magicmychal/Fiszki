package eu.qm.fiszki.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import eu.qm.fiszki.R;
import eu.qm.fiszki.tutorial.TutorialActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        SharedPreferences runCheck = getSharedPreferences("hasRunBefore", Context.MODE_PRIVATE);
        boolean hasRun = runCheck.getBoolean("hasRun", false);
        if (!hasRun) {
            runCheck.edit().putBoolean("hasRun", true).apply();
            startActivity(new Intent(this, TutorialActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}

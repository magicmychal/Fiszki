package eu.qm.fiszki.tutorial;

/**
 * Created by bgood on 2016-04-14.
 */

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import eu.qm.fiszki.NightModeController;
import eu.qm.fiszki.R;
import eu.qm.fiszki.activity.MainActivity;
import eu.qm.fiszki.tutorial.page.ColorPage;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new NightModeController(this).useTheme();
        setContentView(R.layout.activity_tutorial);

        if (savedInstanceState == null) {
            replaceTutorialFragment();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void replaceTutorialFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, new ColorPage());
        fragmentTransaction.commit();
    }

    public void goButton(View view) {
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }


    public void skipButton(View view) {
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }
}

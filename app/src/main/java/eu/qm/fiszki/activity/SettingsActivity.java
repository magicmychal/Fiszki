package eu.qm.fiszki.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import eu.qm.fiszki.AlarmReceiver;
import eu.qm.fiszki.LocalSharedPreferences;
import eu.qm.fiszki.NightModeController;
import eu.qm.fiszki.R;
import eu.qm.fiszki.model.category.CategoryRepository;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;
import eu.qm.fiszki.tutorial.TutorialActivity;

public class SettingsActivity extends AppCompatActivity {

    private NightModeController mNightModeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNightModeController = new NightModeController(this);
        mNightModeController.useTheme();
        setContentView(R.layout.activity_settings);

        buildToolbar();
        buildNightModeSwitch();
        buildClearData();
        buildSendFeedback();
        buildTutorial();
        buildVersion();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void buildToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings_toolbar_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void buildNightModeSwitch() {
        SwitchCompat nightModeSwitch = findViewById(R.id.settings_night_mode_switch);
        nightModeSwitch.setChecked(mNightModeController.getStatus() != 0);
        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(SettingsActivity.this, R.string.drawer_nightmode_toast_on, Toast.LENGTH_SHORT).show();
                    mNightModeController.on();
                } else {
                    Toast.makeText(SettingsActivity.this, R.string.drawer_nightmode_toast_off, Toast.LENGTH_SHORT).show();
                    mNightModeController.off();
                }
                new ChangeActivityManager(SettingsActivity.this).resetMain();
            }
        });
    }

    private void buildClearData() {
        findViewById(R.id.settings_clear_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setMessage(R.string.alert_clear_database_settings)
                        .setPositiveButton(R.string.button_action_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteDbRows();
                                new CategoryRepository(SettingsActivity.this).addSystemCategory();
                                Toast.makeText(SettingsActivity.this, R.string.drawer_clear_name, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.button_action_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).show();
            }
        });
    }

    private void deleteDbRows() {
        FlashcardRepository flashcardRepository = new FlashcardRepository(this);
        CategoryRepository categoryRepository = new CategoryRepository(this);
        LocalSharedPreferences localSharedPreferences = new LocalSharedPreferences(this);
        AlarmReceiver alarm = new AlarmReceiver();

        flashcardRepository.deleteFlashcards(flashcardRepository.getAllFlashcards());
        categoryRepository.deleteCategories(categoryRepository.getAllCategory());
        alarm.close(this);
        localSharedPreferences.setNotificationPosition(0);
        localSharedPreferences.setNotificationStatus(0);
    }

    private void buildSendFeedback() {
        findViewById(R.id.settings_send_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:fiszki@quickclicker.click"));
                startActivity(emailIntent);
            }
        });
    }

    private void buildTutorial() {
        findViewById(R.id.settings_tutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, TutorialActivity.class));
                finish();
            }
        });
    }

    private void buildVersion() {
        TextView versionText = findViewById(R.id.settings_version);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionText.setText(getString(R.string.drawer_version_ver) + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            versionText.setVisibility(View.GONE);
        }
    }
}

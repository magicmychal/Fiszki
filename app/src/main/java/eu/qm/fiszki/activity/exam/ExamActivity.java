package eu.qm.fiszki.activity.exam;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import eu.qm.fiszki.NightModeController;
import eu.qm.fiszki.R;
import eu.qm.fiszki.dialogs.exam.SetRangeExamDialog;
import eu.qm.fiszki.dialogs.exam.SetRepeatExamDialog;
import eu.qm.fiszki.listeners.exam.ExamGoExaming;

public class ExamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new NightModeController(this).useTheme();
        setContentView(R.layout.activity_exam);

        buildToolbar();
        buildFAB();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void buildToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.exam_toolbar_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void buildFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new ExamGoExaming(this));
    }

    public void examRangeClick(View view) {
        new SetRangeExamDialog(this).show();
    }

    public void examRepeatClick(View view) {
        new SetRepeatExamDialog(this).show();
    }
}

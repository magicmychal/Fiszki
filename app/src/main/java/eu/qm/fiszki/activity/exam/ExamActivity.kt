package eu.qm.fiszki.activity.exam

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.dialogs.exam.SetRangeExamDialog
import eu.qm.fiszki.dialogs.exam.SetRepeatExamDialog
import eu.qm.fiszki.listeners.exam.ExamGoExaming

class ExamActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_exam)

        buildToolbar()
        buildFAB()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun buildToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.exam_toolbar_title)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun buildFAB() {
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(ExamGoExaming(this))
    }

    fun examRangeClick(view: View) {
        SetRangeExamDialog(this).show()
    }

    fun examRepeatClick(view: View) {
        SetRepeatExamDialog(this).show()
    }
}

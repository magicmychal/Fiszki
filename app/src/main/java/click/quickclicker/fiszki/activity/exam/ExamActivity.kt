package click.quickclicker.fiszki.activity.exam

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.activity.SettingsActivity
import click.quickclicker.fiszki.activity.learning.LearningActivity
import click.quickclicker.fiszki.activity.myWords.category.CategoryActivity
import click.quickclicker.fiszki.dialogs.exam.SetRangeExamDialog
import click.quickclicker.fiszki.dialogs.exam.SetRepeatExamDialog
import click.quickclicker.fiszki.listeners.exam.ExamGoExaming

class ExamActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        OrientationHelper.lockPortraitOnPhone(this)
        setContentView(R.layout.activity_exam)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        buildToolbar()
        buildFAB()
        buildBottomNav()
    }

    private fun buildToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.exam_toolbar_title)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun buildFAB() {
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(ExamGoExaming(this))
    }

    private fun buildBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_exam

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_exam -> true
                R.id.nav_flashcards -> {
                    startActivity(Intent(this, CategoryActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_learning -> {
                    startActivity(Intent(this, LearningActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    fun examRangeClick(view: View) {
        SetRangeExamDialog(this).show()
    }

    fun examRepeatClick(view: View) {
        SetRepeatExamDialog(this).show()
    }
}

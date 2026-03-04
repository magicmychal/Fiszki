package eu.qm.fiszki.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.exam.ExamFragment
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.activity.learning.LearningFragment
import eu.qm.fiszki.activity.myWords.category.CategoryFragment

class NavHostActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TAB = "tab"
    }

    private lateinit var bottomNav: BottomNavigationView

    private var categoryFragment: CategoryFragment? = null
    private var learningFragment: LearningFragment? = null
    private var examFragment: ExamFragment? = null
    private var settingsFragment: SettingsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_nav_host)

        bottomNav = findViewById(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_flashcards -> { showFragment(getOrCreateCategoryFragment()); true }
                R.id.nav_learning -> { showFragment(getOrCreateLearningFragment()); true }
                R.id.nav_exam -> { showFragment(getOrCreateExamFragment()); true }
                R.id.nav_settings -> { showFragment(getOrCreateSettingsFragment()); true }
                else -> false
            }
        }

        CategoryRepository(this).addSystemCategory()

        if (savedInstanceState == null) {
            val tabId = intent.getIntExtra(EXTRA_TAB, 0)
            if (tabId != 0) {
                bottomNav.selectedItemId = tabId
            } else {
                bottomNav.selectedItemId = R.id.nav_flashcards
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val tabId = intent.getIntExtra(EXTRA_TAB, -1)
        if (tabId != -1) {
            bottomNav.selectedItemId = tabId
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun getOrCreateCategoryFragment(): CategoryFragment {
        if (categoryFragment == null) categoryFragment = CategoryFragment()
        return categoryFragment!!
    }

    private fun getOrCreateLearningFragment(): LearningFragment {
        if (learningFragment == null) learningFragment = LearningFragment()
        return learningFragment!!
    }

    private fun getOrCreateExamFragment(): ExamFragment {
        if (examFragment == null) examFragment = ExamFragment()
        return examFragment!!
    }

    private fun getOrCreateSettingsFragment(): SettingsFragment {
        if (settingsFragment == null) settingsFragment = SettingsFragment()
        return settingsFragment!!
    }
}

package click.quickclicker.fiszki.activity.myWords.category

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.activity.SettingsActivity
import click.quickclicker.fiszki.activity.exam.ExamActivity
import click.quickclicker.fiszki.activity.learning.LearningActivity
import click.quickclicker.fiszki.dialogs.category.AddCategoryDialog
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class CategoryActivity : AppCompatActivity() {

    private lateinit var mActivity: Activity
    private lateinit var mEmptyText: TextView
    private lateinit var mRecycleView: RecyclerView
    private lateinit var mCategoryRepository: CategoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        OrientationHelper.lockPortraitOnPhone(this)
        setContentView(R.layout.category_activity)
        init()
        buildAddButton()
        buildList()
        buildBottomNav()
    }

    private fun init() {
        mActivity = this
        mCategoryRepository = CategoryRepository(mActivity)
        mEmptyText = mActivity.findViewById(R.id.empty_word_text)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            updateList()
        }
    }

    private fun buildAddButton() {
        val addBtn = findViewById<ImageButton>(R.id.btn_add_category)
        addBtn.setOnClickListener {
            AddCategoryDialog(mActivity).show()
        }
    }

    private fun buildList() {
        mRecycleView = findViewById(R.id.listview_category)
        mRecycleView.layoutManager = LinearLayoutManager(this)
    }

    private fun buildBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_flashcards

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_flashcards -> true
                R.id.nav_learning -> {
                    startActivity(Intent(mActivity, LearningActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_exam -> {
                    startActivity(Intent(mActivity, ExamActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(mActivity, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun updateList() {
        val categories = ArrayList<Category>()
        val uncategoryFlashcards = FlashcardRepository(mActivity).getFlashcardsByCategoryID(1)

        if (uncategoryFlashcards.isNotEmpty()) {
            mCategoryRepository.getCategoryByID(1)?.let { categories.add(it) }
        }
        categories.addAll(mCategoryRepository.getUserCategory())

        if (categories.isEmpty()) {
            mEmptyText.visibility = View.VISIBLE
        } else {
            mEmptyText.visibility = View.INVISIBLE
        }

        val adapter = CategoryShowAdapter(mActivity, categories)
        mRecycleView.swapAdapter(adapter, false)
    }
}

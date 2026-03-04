package eu.qm.fiszki.activity.myWords.category

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.dialogs.category.AddCategoryDialog
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class CategoryActivity : AppCompatActivity() {

    private lateinit var mActivity: Activity
    private lateinit var mEmptyText: TextView
    private lateinit var mRecycleView: RecyclerView
    private lateinit var mCategoryRepository: CategoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.category_activity)
        init()
        buildToolbar()
        buildFAB()
        buildList()
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        mActivity.finish()
    }

    private fun buildFAB() {
        val fab = findViewById<FloatingActionButton>(R.id.addFAB)
        fab.setOnClickListener {
            AddCategoryDialog(mActivity).show()
        }
    }

    private fun buildToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_exit_to_app_24px)
        toolbar.setTitle(R.string.category_activity_title)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun buildList() {
        mRecycleView = findViewById(R.id.listview_category)
        val staggeredLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        mRecycleView.layoutManager = staggeredLayoutManager
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

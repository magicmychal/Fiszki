package eu.qm.fiszki.activity.myWords.flashcards

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.activity.defaultCategoryColor
import eu.qm.fiszki.activity.findCategoryColor
import eu.qm.fiszki.activity.myWords.CategoryManagerSingleton
import eu.qm.fiszki.dialogs.category.EditCategoryBottomSheet
import eu.qm.fiszki.dialogs.flashcard.AddFlashcardDialog
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class FlashcardsActivity : AppCompatActivity() {

    private lateinit var mActivity: Activity
    private lateinit var mEmptyFlashcard: TextView
    private lateinit var mRecycleView: RecyclerView
    private lateinit var mCurrentCategory: Category
    private lateinit var mFlashcardRepository: FlashcardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.flashcards_activity)
        init()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                @Suppress("DEPRECATION")
                overridePendingTransition(R.anim.right_out, R.anim.left_in)
            }
        })
        buildHeroHeader()
        buildActionChips()
        buildListView()
    }

    private fun init() {
        mActivity = this
        mFlashcardRepository = FlashcardRepository(mActivity)
        mEmptyFlashcard = findViewById(R.id.empty_category_text)
        mCurrentCategory = CategoryRepository(mActivity)
            .getCategoryByID(CategoryManagerSingleton.currentCategoryId)!!
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            mCurrentCategory = CategoryRepository(mActivity)
                .getCategoryByID(CategoryManagerSingleton.currentCategoryId)!!
            buildHeroHeader()
            updateListView()
        }
    }

    private fun buildHeroHeader() {
        val categoryName = findViewById<TextView>(R.id.hero_category_name)
        val subtitle = findViewById<TextView>(R.id.hero_subtitle)

        categoryName.text = mCurrentCategory.getCategory() ?: getString(R.string.flashcard_toolbar_null_category)

        val langFrom = mCurrentCategory.getLangFrom()
        val langOn = mCurrentCategory.getLangOn()
        if (!langFrom.isNullOrEmpty() && !langOn.isNullOrEmpty()) {
            subtitle.text = "$langFrom → $langOn"
        } else {
            subtitle.visibility = View.GONE
        }

        // Apply category color to hero gradient + status bar
        val catColor = findCategoryColor(mCurrentCategory.getColor()) ?: defaultCategoryColor()
        val heroHeader = findViewById<View>(R.id.hero_header)
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(catColor.primary, catColor.container)
        )
        heroHeader.background = gradient
        @Suppress("DEPRECATION")
        window.statusBarColor = catColor.primary
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        findViewById<View>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun buildActionChips() {
        findViewById<MaterialButton>(R.id.chip_add_card).setOnClickListener {
            AddFlashcardDialog(mActivity, mCurrentCategory.id).show()
        }

        findViewById<MaterialButton>(R.id.chip_start_review).setOnClickListener {
            val flashcards = mFlashcardRepository.getFlashcardsByCategoryID(mCurrentCategory.id)
            if (flashcards.isEmpty()) {
                Toast.makeText(mActivity, R.string.flashcard_empty_text, Toast.LENGTH_SHORT).show()
            } else {
                ChangeActivityManager(mActivity).goToLearningCheck(flashcards)
            }
        }

        findViewById<MaterialButton>(R.id.chip_edit_category).setOnClickListener {
            val bottomSheet = EditCategoryBottomSheet.newInstance(mCurrentCategory.id)
            bottomSheet.show(supportFragmentManager, "EditCategoryBottomSheet")
        }
    }

    private fun buildListView() {
        mRecycleView = findViewById(R.id.listview_flashcard)
        mRecycleView.layoutManager = LinearLayoutManager(this)
        attachSwipeToDelete()
    }

    private fun attachSwipeToDelete() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val flashcards = mFlashcardRepository.getFlashcardsByCategoryID(mCurrentCategory.id)
                if (position in flashcards.indices) {
                    val flashcard = flashcards[position]
                    mFlashcardRepository.deleteFlashcard(flashcard)
                }
                updateListView()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (dX < 0) {
                    val itemView = viewHolder.itemView
                    val backgroundColor = MaterialColors.getColor(itemView, android.R.attr.colorError)
                    val iconColor = MaterialColors.getColor(itemView, com.google.android.material.R.attr.colorOnError)
                    val paint = Paint().apply { color = backgroundColor }

                    val background = RectF(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                    c.drawRect(background, paint)

                    val deleteIcon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_delete_24)?.mutate()
                    if (deleteIcon != null) {
                        deleteIcon.setTint(iconColor)
                        val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        val iconBottom = iconTop + deleteIcon.intrinsicHeight
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        deleteIcon.draw(c)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(mRecycleView)
    }

    private fun updateListView() {
        val flashcards = mFlashcardRepository.getFlashcardsByCategoryID(mCurrentCategory.id)

        if (flashcards.isEmpty()) {
            mEmptyFlashcard.visibility = View.VISIBLE
        } else {
            mEmptyFlashcard.visibility = View.GONE
        }

        val catColor = findCategoryColor(mCurrentCategory.getColor()) ?: defaultCategoryColor()
        val adapter = FlashcardShowAdapter(mActivity, flashcards, catColor.primary)
        mRecycleView.swapAdapter(adapter, false)
    }
}

package click.quickclicker.fiszki.activity.myWords.flashcards

import android.app.Activity
import android.graphics.Canvas
import androidx.activity.enableEdgeToEdge
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import click.quickclicker.fiszki.LocalSharedPreferences
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.activity.ChangeActivityManager
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.activity.findCategoryColor
import click.quickclicker.fiszki.activity.myWords.CategoryManagerSingleton
import click.quickclicker.fiszki.dialogs.category.EditCategoryBottomSheet
import click.quickclicker.fiszki.dialogs.flashcard.AddFlashcardDialog
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class FlashcardsActivity : AppCompatActivity() {

    private lateinit var mActivity: Activity
    private lateinit var mEmptyFlashcard: TextView
    private lateinit var mRecycleView: RecyclerView
    private lateinit var mCurrentCategory: Category
    private lateinit var mFlashcardRepository: FlashcardRepository
    private var mLastFingerprint: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)
        setContentView(R.layout.flashcards_activity)
        init()
        handleWindowInsets()
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

    private fun handleWindowInsets() {
        // Add top margin to back button so it sits below the status bar
        val backButton = findViewById<ImageButton>(R.id.btn_back)
        val originalTopMargin = 16 // dp value from XML
        ViewCompat.setOnApplyWindowInsetsListener(backButton) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            val lp = v.layoutParams as android.widget.FrameLayout.LayoutParams
            lp.topMargin = originalTopMargin.dpToPx() + bars.top
            v.layoutParams = lp
            WindowInsetsCompat.CONSUMED
        }

        // Add bottom padding to RecyclerView so content scrolls above the nav bar
        val recyclerView = findViewById<RecyclerView>(R.id.listview_flashcard)
        val originalBottomPadding = recyclerView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.updatePadding(bottom = originalBottomPadding + bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            val cat = CategoryRepository(mActivity)
                .getCategoryByID(CategoryManagerSingleton.currentCategoryId)
            if (cat == null) {
                finish()
                return
            }
            mCurrentCategory = cat
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

        val fingerprint = flashcards.map { "${it.id}:${it.word}:${it.translation}:${it.priority}:${it.fsrsLastRating}" }
        if (fingerprint == mLastFingerprint) return
        mLastFingerprint = fingerprint

        if (flashcards.isEmpty()) {
            mEmptyFlashcard.visibility = View.VISIBLE
        } else {
            mEmptyFlashcard.visibility = View.GONE
        }

        val catColor = findCategoryColor(mCurrentCategory.getColor()) ?: defaultCategoryColor()
        val useFsrs = LocalSharedPreferences(mActivity).useFsrsAlgorithm
        val adapter = FlashcardShowAdapter(mActivity, flashcards, catColor.primary, useFsrs)
        mRecycleView.swapAdapter(adapter, false)
    }
}

package click.quickclicker.fiszki.activity.myWords

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import click.quickclicker.fiszki.LocalSharedPreferences
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.ChangeActivityManager
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.activity.findCategoryColor
import click.quickclicker.fiszki.dialogs.category.EditCategoryBottomSheet
import click.quickclicker.fiszki.dialogs.flashcard.AddFlashcardDialog
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import click.quickclicker.fiszki.activity.myWords.flashcards.FlashcardShowAdapter

class FlashcardDetailFragment : Fragment() {

    companion object {
        private const val ARG_CATEGORY_ID = "category_id"

        fun newInstance(categoryId: Int): FlashcardDetailFragment {
            return FlashcardDetailFragment().apply {
                arguments = Bundle().apply { putInt(ARG_CATEGORY_ID, categoryId) }
            }
        }
    }

    var onCategoryDeleted: (() -> Unit)? = null

    private var categoryId: Int = 0
    private lateinit var flashcardRepository: FlashcardRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var currentCategory: Category

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.flashcards_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryId = arguments?.getInt(ARG_CATEGORY_ID) ?: return
        flashcardRepository = FlashcardRepository(requireActivity())
        categoryRepository = CategoryRepository(requireActivity())
        currentCategory = categoryRepository.getCategoryByID(categoryId) ?: return

        emptyText = view.findViewById(R.id.empty_category_text)
        recyclerView = view.findViewById(R.id.listview_flashcard)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        buildHeroHeader(view)
        buildActionChips(view)
        attachSwipeToDelete()
        updateList()
    }

    override fun onResume() {
        super.onResume()
        currentCategory = categoryRepository.getCategoryByID(categoryId) ?: return
        view?.let { buildHeroHeader(it) }
        updateList()
    }

    private fun buildHeroHeader(view: View) {
        val categoryName = view.findViewById<TextView>(R.id.hero_category_name)
        val subtitle = view.findViewById<TextView>(R.id.hero_subtitle)

        categoryName.text = currentCategory.getCategory()

        val langFrom = currentCategory.getLangFrom()
        val langOn = currentCategory.getLangOn()
        if (!langFrom.isNullOrEmpty() && !langOn.isNullOrEmpty()) {
            subtitle.text = "$langFrom \u2192 $langOn"
            subtitle.visibility = View.VISIBLE
        } else {
            subtitle.visibility = View.GONE
        }

        val catColor = findCategoryColor(currentCategory.getColor()) ?: defaultCategoryColor()
        val heroHeader = view.findViewById<View>(R.id.hero_header)
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(catColor.primary, catColor.container)
        )
        heroHeader.background = gradient

        // Hide back button in tablet split-view
        view.findViewById<View>(R.id.btn_back).visibility = View.GONE
    }

    private fun buildActionChips(view: View) {
        view.findViewById<MaterialButton>(R.id.chip_add_card).setOnClickListener {
            AddFlashcardDialog(requireActivity(), currentCategory.id).show()
                .setOnDismissListener { updateList() }
        }

        view.findViewById<MaterialButton>(R.id.chip_start_review).setOnClickListener {
            val flashcards = flashcardRepository.getFlashcardsByCategoryID(currentCategory.id)
            if (flashcards.isEmpty()) {
                Toast.makeText(requireContext(), R.string.flashcard_empty_text, Toast.LENGTH_SHORT).show()
            } else {
                ChangeActivityManager(requireActivity()).goToLearningCheck(flashcards)
            }
        }

        view.findViewById<MaterialButton>(R.id.chip_edit_category).setOnClickListener {
            val bottomSheet = EditCategoryBottomSheet.newInstance(currentCategory.id)
            bottomSheet.show(childFragmentManager, "EditCategoryBottomSheet")
            childFragmentManager.executePendingTransactions()
            bottomSheet.dialog?.setOnDismissListener {
                bottomSheet.dismiss()
                val updated = categoryRepository.getCategoryByID(categoryId)
                if (updated == null) {
                    onCategoryDeleted?.invoke()
                    return@setOnDismissListener
                }
                currentCategory = updated
                view?.let { v -> buildHeroHeader(v) }
                updateList()
            }
        }
    }

    private fun attachSwipeToDelete() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val flashcards = flashcardRepository.getFlashcardsByCategoryID(currentCategory.id)
                if (position in flashcards.indices) {
                    flashcardRepository.deleteFlashcard(flashcards[position])
                }
                updateList()
            }

            override fun onChildDraw(c: Canvas, rv: RecyclerView, vh: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isActive: Boolean) {
                if (dX < 0) {
                    val itemView = vh.itemView
                    val bgColor = MaterialColors.getColor(itemView, android.R.attr.colorError)
                    val iconColor = MaterialColors.getColor(itemView, com.google.android.material.R.attr.colorOnError)
                    val paint = Paint().apply { color = bgColor }
                    val bg = RectF(itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                    c.drawRect(bg, paint)
                    val deleteIcon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_delete_24)?.mutate()
                    if (deleteIcon != null) {
                        deleteIcon.setTint(iconColor)
                        val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconTop + deleteIcon.intrinsicHeight)
                        deleteIcon.draw(c)
                    }
                }
                super.onChildDraw(c, rv, vh, dX, dY, actionState, isActive)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }

    private fun updateList() {
        val flashcards = flashcardRepository.getFlashcardsByCategoryID(currentCategory.id)
        emptyText.visibility = if (flashcards.isEmpty()) View.VISIBLE else View.GONE
        val catColor = findCategoryColor(currentCategory.getColor()) ?: defaultCategoryColor()
        val useFsrs = LocalSharedPreferences(requireContext()).useFsrsAlgorithm
        val adapter = FlashcardShowAdapter(requireActivity(), flashcards, catColor.primary, useFsrs)
        recyclerView.swapAdapter(adapter, false)
    }
}

package click.quickclicker.fiszki.activity.myWords.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.dialogs.category.AddCategoryDialog
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class CategoryFragment : Fragment() {

    private lateinit var mEmptyText: TextView
    private lateinit var mRecycleView: RecyclerView
    private lateinit var mCategoryRepository: CategoryRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.category_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCategoryRepository = CategoryRepository(requireActivity())
        mEmptyText = view.findViewById(R.id.empty_word_text)
        buildAddButton(view)
        buildList(view)
        handleWindowInsets(view)
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun buildAddButton(view: View) {
        val addBtn = view.findViewById<ImageButton>(R.id.btn_add_category)
        addBtn.setOnClickListener {
            val dialog = AddCategoryDialog(requireActivity()).show()
            dialog.setOnDismissListener { updateList() }
        }
    }

    private fun buildList(view: View) {
        mRecycleView = view.findViewById(R.id.listview_category)
        mRecycleView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun handleWindowInsets(view: View) {
        val contentLayout = view.findViewById<LinearLayout>(R.id.category_content_layout)
        ViewCompat.setOnApplyWindowInsetsListener(contentLayout) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun updateList() {
        val activity = activity ?: return
        val categories = ArrayList<Category>()
        val uncategoryFlashcards = FlashcardRepository(activity).getFlashcardsByCategoryID(1)

        if (uncategoryFlashcards.isNotEmpty()) {
            mCategoryRepository.getCategoryByID(1)?.let { categories.add(it) }
        }
        categories.addAll(mCategoryRepository.getUserCategory())

        if (categories.isEmpty()) {
            mEmptyText.visibility = View.VISIBLE
        } else {
            mEmptyText.visibility = View.INVISIBLE
        }

        val adapter = CategoryShowAdapter(activity, categories)
        mRecycleView.swapAdapter(adapter, false)
    }
}



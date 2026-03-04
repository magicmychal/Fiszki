package eu.qm.fiszki.activity.myWords.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.qm.fiszki.R
import eu.qm.fiszki.dialogs.category.AddCategoryDialog
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.FlashcardRepository

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

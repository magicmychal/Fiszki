package eu.qm.fiszki.activity.myWords.category

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.myWords.CategoryManagerSingleton
import eu.qm.fiszki.activity.myWords.flashcards.FlashcardsActivity
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class CategoryShowAdapter(
    private val activity: Activity,
    private val arrayList: ArrayList<Category>
) : RecyclerView.Adapter<CategoryShowAdapter.ViewHolder>() {

    private val flashcardRepository = FlashcardRepository(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_show_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = arrayList[position]

        holder.name.text = category.getCategory()
        setLanguageText(holder, category)

        val count = flashcardRepository.getFlashcardsByCategoryID(category.id).size
        holder.meta.text = "$count cards"

        holder.card.setOnClickListener {
            CategoryManagerSingleton.currentCategoryId = category.id
            activity.startActivity(Intent(activity, FlashcardsActivity::class.java))
            activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }

    private fun setLanguageText(holder: ViewHolder, category: Category) {
        var langFrom = category.getLangFrom()
        var langOn = category.getLangOn()

        if (langFrom == null && langOn == null) {
            holder.lang.setText(R.string.category_no_lang)
        } else {
            if (langFrom!!.isEmpty() && langOn!!.isEmpty()) {
                holder.lang.setText(R.string.category_no_lang)
            } else {
                if (langFrom.isEmpty()) {
                    langFrom = activity.getString(R.string.category_no_lang)
                }
                if (langOn!!.isEmpty()) {
                    langOn = activity.getString(R.string.category_no_lang)
                }
                holder.lang.text = "$langFrom - $langOn"
            }
        }
    }

    override fun getItemCount(): Int = arrayList.size

    override fun getItemViewType(position: Int): Int = position

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.placeCard)
        val name: TextView = itemView.findViewById(R.id.category_name)
        val lang: TextView = itemView.findViewById(R.id.category_lang)
        val meta: TextView = itemView.findViewById(R.id.category_meta)
    }
}

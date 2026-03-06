package eu.qm.fiszki.activity.myWords.flashcards

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.qm.fiszki.R
import eu.qm.fiszki.dialogs.flashcard.EditAndDeleteFlashcardDialog
import eu.qm.fiszki.model.flashcard.Flashcard

class FlashcardShowAdapter(
    private val activity: Activity,
    private val arrayList: ArrayList<Flashcard>
) : RecyclerView.Adapter<FlashcardShowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.flashcards_show_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val flashcard = arrayList[position]

        holder.word.text = flashcard.getWord()
        holder.translation.text = flashcard.getTranslation()
        setStarRating(holder, flashcard.priority)

        holder.itemView.setOnClickListener(DoubleClickListener {
            EditAndDeleteFlashcardDialog(activity, flashcard).show()
        })
    }

    override fun getItemCount(): Int = arrayList.size

    private fun setStarRating(holder: ViewHolder, priority: Int) {
        val stars = holder.starViews
        for (i in stars.indices) {
            stars[i].setImageResource(
                if (i < priority) R.drawable.ic_star_filled else R.drawable.ic_star_empty
            )
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val word: TextView = itemView.findViewById(R.id.flashcard_word)
        val translation: TextView = itemView.findViewById(R.id.flashcard_translate)
        private val starLayout: LinearLayout = itemView.findViewById(R.id.star_rating)
        val starViews: List<ImageView> = listOf(
            itemView.findViewById(R.id.star1),
            itemView.findViewById(R.id.star2),
            itemView.findViewById(R.id.star3),
            itemView.findViewById(R.id.star4),
            itemView.findViewById(R.id.star5)
        )
    }

    private class DoubleClickListener(
        private val onDoubleClick: () -> Unit
    ) : View.OnClickListener {
        private var lastClickTime = 0L

        override fun onClick(v: View) {
            val now = System.currentTimeMillis()
            if (now - lastClickTime < 300) {
                onDoubleClick()
                lastClickTime = 0
            } else {
                lastClickTime = now
            }
        }
    }
}

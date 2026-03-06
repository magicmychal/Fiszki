package eu.qm.fiszki.listeners.exam

import android.app.Activity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class ExamGoExaming(private val activity: Activity) : View.OnClickListener {

    private var repeat: Int = 0
    private lateinit var rangeText: String
    private lateinit var repeatText: String

    override fun onClick(view: View) {
        getValueFromCards()
        if (checkChosen()) {
            repeat = repeatText.toInt()
            val chosenFlashcard = FlashcardRepository(activity)
                .getFlashcardsByCategoryID(
                    CategoryRepository(activity).getCategoryByName(rangeText)!!.id
                )
            if (chosenFlashcard.isEmpty()) {
                Toast.makeText(activity, R.string.exam_range_empty_toast, Toast.LENGTH_LONG).show()
            } else {
                val category = CategoryRepository(activity).getCategoryByName(rangeText)
                val categoryName = category?.getCategory()
                val languagePair = if (category != null && !category.getLangFrom().isNullOrEmpty() && !category.getLangOn().isNullOrEmpty()) {
                    "${category.getLangFrom()} to ${category.getLangOn()}"
                } else null
                ChangeActivityManager(activity).goToExamCheck(chosenFlashcard, repeat, categoryName, languagePair)
            }
        }
    }

    private fun getValueFromCards() {
        rangeText = (activity.findViewById<TextView>(R.id.exam_range_text)).text.toString()
        repeatText = (activity.findViewById<TextView>(R.id.exam_repeat_text)).text.toString()
    }

    private fun checkChosen(): Boolean {
        if (rangeText == activity.resources.getString(R.string.exam_card_range_title)) {
            Toast.makeText(activity, R.string.exam_no_chosen_range, Toast.LENGTH_SHORT).show()
            return false
        }
        if (repeatText == activity.resources.getString(R.string.exam_card_repeat_title)) {
            Toast.makeText(activity, R.string.exam_no_chosen_repeat, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}

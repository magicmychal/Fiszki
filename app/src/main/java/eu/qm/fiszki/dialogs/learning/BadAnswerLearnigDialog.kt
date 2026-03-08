package eu.qm.fiszki.dialogs.learning

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.learning.LearningCheckActivity
import eu.qm.fiszki.model.flashcard.Flashcard

class BadAnswerLearnigDialog(
    context: Context,
    flashcard: Flashcard,
    lca: LearningCheckActivity,
    correctAnswer: String = flashcard.getTranslation(),
    userAnswer: String = ""
) {

    init {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_bad_answer, null)

        // Set up the text views with highlighted differences
        val userAnswerTextView = dialogView.findViewById<TextView>(R.id.user_answer_text)
        val correctAnswerTextView = dialogView.findViewById<TextView>(R.id.correct_answer_text)

        // Build spannable strings with highlights
        userAnswerTextView.text = buildHighlightedUserAnswer(userAnswer, correctAnswer)
        correctAnswerTextView.text = buildHighlightedCorrectAnswer(correctAnswer, userAnswer)

        val dialog = MaterialAlertDialogBuilder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Set up button listeners
        dialogView.findViewById<MaterialButton>(R.id.btn_skip).setOnClickListener {
            lca.drawFlashcard()
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_retry).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun buildHighlightedUserAnswer(userAnswer: String, correctAnswer: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder(userAnswer.ifEmpty { "—" })

        if (userAnswer.isNotEmpty()) {
            highlightDiffs(builder, 0, userAnswer, correctAnswer, RED)
        }

        return builder
    }

    private fun buildHighlightedCorrectAnswer(correctAnswer: String, userAnswer: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder(correctAnswer)

        if (userAnswer.isNotEmpty()) {
            highlightDiffs(builder, 0, correctAnswer, userAnswer, GREEN)
        }

        return builder
    }


    /**
     * Highlights characters in [text] (starting at [offset] in [builder])
     * that differ from [reference] using the given [color].
     *
     * Uses a simple char-by-char comparison. Extra characters in [text]
     * beyond [reference] length are also highlighted.
     */
    private fun highlightDiffs(
        builder: SpannableStringBuilder,
        offset: Int,
        text: String,
        reference: String,
        color: Int
    ) {
        var i = 0
        while (i < text.length) {
            val differs = i >= reference.length || text[i] != reference[i]
            if (differs) {
                // Find the end of this differing run
                var j = i + 1
                while (j < text.length && (j >= reference.length || text[j] != reference[j])) {
                    j++
                }
                builder.setSpan(
                    ForegroundColorSpan(color),
                    offset + i, offset + j,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.setSpan(
                    StyleSpan(android.graphics.Typeface.BOLD),
                    offset + i, offset + j,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                i = j
            } else {
                i++
            }
        }
    }

    companion object {
        private val RED = Color.parseColor("#D32F2F")
        private val GREEN = Color.parseColor("#388E3C")

        // Keep this for backward compatibility with ExamCheckActivity
        @Deprecated("Use the new dialog layout instead")
        fun buildDiffMessage(
            context: Context,
            correctAnswer: String,
            userAnswer: String
        ): SpannableStringBuilder {
            val builder = SpannableStringBuilder()

            // "Your answer:" label + highlighted user answer
            val yourAnswerLabel = context.getString(R.string.learning_check_dialog_your_answer)
            builder.append(yourAnswerLabel)
            builder.append("\n")

            val userStart = builder.length
            builder.append(userAnswer.ifEmpty { "—" })
            val userEnd = builder.length

            if (userAnswer.isNotEmpty()) {
                highlightDiffsStatic(builder, userStart, userAnswer, correctAnswer, RED)
            }

            builder.append("\n\n")

            // "Correctly:" label + highlighted correct answer
            val correctLabel = context.getString(R.string.learning_check_dialog_bad_answer_1)
            builder.append(correctLabel)
            builder.append("\n")

            val correctStart = builder.length
            builder.append(correctAnswer)
            val correctEnd = builder.length

            // Bold the correct answer
            builder.setSpan(
                StyleSpan(android.graphics.Typeface.BOLD),
                correctStart, correctEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (userAnswer.isNotEmpty()) {
                highlightDiffsStatic(builder, correctStart, correctAnswer, userAnswer, GREEN)
            }

            builder.append("\n\n")
            builder.append(context.getString(R.string.learning_check_dialog_bad_answer_2))

            return builder
        }

        private fun highlightDiffsStatic(
            builder: SpannableStringBuilder,
            offset: Int,
            text: String,
            reference: String,
            color: Int
        ) {
            var i = 0
            while (i < text.length) {
                val differs = i >= reference.length || text[i] != reference[i]
                if (differs) {
                    // Find the end of this differing run
                    var j = i + 1
                    while (j < text.length && (j >= reference.length || text[j] != reference[j])) {
                        j++
                    }
                    builder.setSpan(
                        ForegroundColorSpan(color),
                        offset + i, offset + j,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    builder.setSpan(
                        StyleSpan(android.graphics.Typeface.BOLD),
                        offset + i, offset + j,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    i = j
                } else {
                    i++
                }
            }
        }
    }
}

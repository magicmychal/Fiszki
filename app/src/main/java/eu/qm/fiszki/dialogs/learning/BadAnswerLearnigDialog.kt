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
import eu.qm.fiszki.Checker
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

        val userAnswerTextView = dialogView.findViewById<TextView>(R.id.user_answer_text)
        val correctAnswerTextView = dialogView.findViewById<TextView>(R.id.correct_answer_text)

        val (userDiffs, correctDiffs) = Checker.alignDiffs(userAnswer, correctAnswer)
        userAnswerTextView.text = buildHighlighted(userAnswer.ifEmpty { "—" }, if (userAnswer.isEmpty()) null else userDiffs, RED)
        correctAnswerTextView.text = buildHighlighted(correctAnswer, correctDiffs, GREEN)

        val dialog = MaterialAlertDialogBuilder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<MaterialButton>(R.id.btn_skip).setOnClickListener {
            lca.drawFlashcard()
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btn_retry).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    companion object {
        private val RED = Color.parseColor("#D32F2F")
        private val GREEN = Color.parseColor("#388E3C")

        /**
         * Builds a SpannableStringBuilder with only the differing characters
         * highlighted in [color] + bold, based on edit-distance alignment.
         */
        private fun buildHighlighted(
            text: String,
            diffs: BooleanArray?,
            color: Int
        ): SpannableStringBuilder {
            val builder = SpannableStringBuilder(text)
            if (diffs == null) return builder

            var i = 0
            while (i < diffs.size) {
                if (diffs[i]) {
                    val start = i
                    while (i < diffs.size && diffs[i]) i++
                    builder.setSpan(
                        ForegroundColorSpan(color),
                        start, i,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    builder.setSpan(
                        StyleSpan(android.graphics.Typeface.BOLD),
                        start, i,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    i++
                }
            }
            return builder
        }

        /**
         * Used by ExamCheckActivity for inline diff display.
         */
        fun buildDiffMessage(
            context: Context,
            correctAnswer: String,
            userAnswer: String
        ): SpannableStringBuilder {
            val (userDiffs, correctDiffs) = Checker.alignDiffs(userAnswer, correctAnswer)

            val builder = SpannableStringBuilder()

            // "Your answer:" label + highlighted user answer
            builder.append(context.getString(R.string.learning_check_dialog_your_answer))
            builder.append("\n")

            val userStart = builder.length
            builder.append(userAnswer.ifEmpty { "—" })

            if (userAnswer.isNotEmpty()) {
                applyDiffSpans(builder, userStart, userDiffs, RED)
            }

            builder.append("\n\n")

            // "Correctly:" label + highlighted correct answer
            builder.append(context.getString(R.string.learning_check_dialog_bad_answer_1))
            builder.append("\n")

            val correctStart = builder.length
            builder.append(correctAnswer)
            val correctEnd = builder.length

            builder.setSpan(
                StyleSpan(android.graphics.Typeface.BOLD),
                correctStart, correctEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (userAnswer.isNotEmpty()) {
                applyDiffSpans(builder, correctStart, correctDiffs, GREEN)
            }

            builder.append("\n\n")
            builder.append(context.getString(R.string.learning_check_dialog_bad_answer_2))

            return builder
        }

        private fun applyDiffSpans(
            builder: SpannableStringBuilder,
            offset: Int,
            diffs: BooleanArray,
            color: Int
        ) {
            var i = 0
            while (i < diffs.size) {
                if (diffs[i]) {
                    val start = i
                    while (i < diffs.size && diffs[i]) i++
                    builder.setSpan(
                        ForegroundColorSpan(color),
                        offset + start, offset + i,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    builder.setSpan(
                        StyleSpan(android.graphics.Typeface.BOLD),
                        offset + start, offset + i,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    i++
                }
            }
        }
    }
}

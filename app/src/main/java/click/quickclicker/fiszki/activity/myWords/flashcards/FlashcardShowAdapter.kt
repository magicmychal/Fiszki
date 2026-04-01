package click.quickclicker.fiszki.activity.myWords.flashcards

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import io.sentry.compose.SentryModifier.sentryTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import android.content.Intent
import click.quickclicker.fiszki.model.flashcard.Flashcard

class FlashcardShowAdapter(
    private val activity: AppCompatActivity,
    private val arrayList: ArrayList<Flashcard>,
    private val categoryColor: Int? = null,
    private val useFsrs: Boolean = false
) : RecyclerView.Adapter<FlashcardShowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val composeView = ComposeView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return ViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val flashcard = arrayList[position]
        holder.composeView.setContent {
            FiszkiTheme {
                FlashcardListItem(
                    word = flashcard.getWord(),
                    translation = flashcard.getTranslation(),
                    priority = flashcard.priority,
                    categoryColor = categoryColor?.let { Color(it or 0xFF000000.toInt()) },
                    useFsrs = useFsrs,
                    lastRating = flashcard.fsrsLastRating,
                    onClick = {
                        activity.startActivity(
                            Intent(activity, EditFlashcardActivity::class.java)
                                .putExtra(EditFlashcardActivity.EXTRA_FLASHCARD_ID, flashcard.id)
                        )
                    }
                )
            }
        }
    }

    override fun getItemCount(): Int = arrayList.size

    class ViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)
}

@Composable
private fun FlashcardListItem(
    word: String,
    translation: String,
    priority: Int,
    categoryColor: Color?,
    useFsrs: Boolean = false,
    lastRating: Int = 0,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .sentryTag("flashcard_list_item"),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Word (bold serif, editorial)
            Text(
                text = word,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = RobotoSerifFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.35f, fill = false)
            )

            // Arrow separator
            Text(
                text = "\u2192",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Translation
            Text(
                text = translation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.45f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (useFsrs) {
                FsrsStateIndicator(
                    lastRating = lastRating,
                    filledColor = categoryColor ?: MaterialTheme.colorScheme.primary
                )
            } else {
                PriorityIndicator(
                    priority = priority,
                    filledColor = categoryColor ?: MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PriorityIndicator(priority: Int, filledColor: Color) {
    val emptyColor = MaterialTheme.colorScheme.outlineVariant
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            val color = if (index < priority) filledColor else emptyColor
            Canvas(modifier = Modifier.size(8.dp)) {
                drawCircle(color = color)
            }
        }
    }
}

@Composable
private fun FsrsStateIndicator(lastRating: Int, filledColor: Color) {
    val emptyColor = MaterialTheme.colorScheme.outlineVariant
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            val ratingValue = index + 1
            val color = if (ratingValue <= lastRating) filledColor else emptyColor
            Canvas(modifier = Modifier.size(8.dp)) {
                drawCircle(color = color)
            }
        }
    }
}

package eu.qm.fiszki.activity.myWords.flashcards

import android.app.Activity
import android.view.ViewGroup
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.recyclerview.widget.RecyclerView
import eu.qm.fiszki.activity.FiszkiTheme
import eu.qm.fiszki.dialogs.flashcard.EditAndDeleteFlashcardDialog
import eu.qm.fiszki.model.flashcard.Flashcard

class FlashcardShowAdapter(
    private val activity: Activity,
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
                    onDoubleClick = {
                        EditAndDeleteFlashcardDialog(activity, flashcard).show()
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
    onDoubleClick: () -> Unit
) {
    val doubleClickListener = doubleClickModifier(onDoubleClick)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(doubleClickListener)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = word,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = translation,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (useFsrs) {
                FsrsStateIndicator(lastRating = lastRating)
            } else {
                PriorityIndicator(
                    priority = priority,
                    filledColor = categoryColor ?: MaterialTheme.colorScheme.primary
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
private fun FsrsStateIndicator(lastRating: Int) {
    val emptyColor = MaterialTheme.colorScheme.outlineVariant
    // Rating values: Again=1, Hard=2, Good=3, Easy=4; 0=none
    val ratingColors = listOf(
        Color(0xFFE53935), // Again — red
        Color(0xFFFB8C00), // Hard — orange
        Color(0xFF43A047), // Good — green
        Color(0xFF1E88E5)  // Easy — blue
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            val ratingValue = index + 1 // 1-based rating
            val color = if (lastRating == ratingValue) ratingColors[index] else emptyColor
            Canvas(modifier = Modifier.size(8.dp)) {
                drawCircle(color = color)
            }
        }
    }
}

@Composable
private fun doubleClickModifier(onDoubleClick: () -> Unit): Modifier {
    var lastClickTime = androidx.compose.runtime.remember { androidx.compose.runtime.mutableLongStateOf(0L) }
    return Modifier.clickable {
        val now = System.currentTimeMillis()
        if (now - lastClickTime.longValue < 300) {
            onDoubleClick()
            lastClickTime.longValue = 0
        } else {
            lastClickTime.longValue = now
        }
    }
}

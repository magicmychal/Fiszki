package eu.qm.fiszki.activity.myWords.flashcards

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    private val arrayList: ArrayList<Flashcard>
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = word,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    StarRating(priority = priority)
                }

                Text(
                    text = translation,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun StarRating(priority: Int) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = if (index < priority) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 2.dp)
            )
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

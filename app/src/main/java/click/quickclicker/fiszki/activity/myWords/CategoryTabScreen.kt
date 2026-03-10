package click.quickclicker.fiszki.activity.myWords

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.ChangeActivityManager
import click.quickclicker.fiszki.activity.findCategoryColor
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.dialogs.category.AddCategoryDialog
import click.quickclicker.fiszki.dialogs.category.EditCategoryBottomSheet
import click.quickclicker.fiszki.dialogs.flashcard.AddFlashcardDialog
import click.quickclicker.fiszki.dialogs.flashcard.EditAndDeleteFlashcardDialog
import click.quickclicker.fiszki.activity.myWords.flashcards.FlashcardsActivity
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

@Composable
fun CategoryTabScreen(
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val categoryRepository = remember { CategoryRepository(context) }
    val flashcardRepository = remember { FlashcardRepository(context) }

    var refreshTrigger by remember { mutableIntStateOf(0) }

    val categories = remember(refreshTrigger) {
        val list = ArrayList<Category>()
        val uncatFlashcards = flashcardRepository.getFlashcardsByCategoryID(1)
        if (uncatFlashcards.isNotEmpty()) {
            categoryRepository.getCategoryByID(1)?.let { list.add(it) }
        }
        list.addAll(categoryRepository.getUserCategory())
        list
    }

    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    // Auto-select first if on tablet and nothing selected
    if (isTablet && selectedCategoryId == null && categories.isNotEmpty()) {
        selectedCategoryId = categories[0].id
    }

    fun refresh() {
        refreshTrigger++
    }

    if (isTablet) {
        Row(modifier = modifier.fillMaxSize()) {
            // Left: category list
            CategoryListPane(
                categories = categories,
                flashcardRepository = flashcardRepository,
                selectedCategoryId = selectedCategoryId,
                onCategoryClick = { selectedCategoryId = it.id },
                onAddCategory = {
                    if (activity != null) {
                        AddCategoryDialog(activity).show().setOnDismissListener { refresh() }
                    }
                },
                modifier = Modifier.width(320.dp).fillMaxHeight()
            )
            VerticalDivider()
            // Right: flashcard detail
            val selectedCategory = selectedCategoryId?.let { id -> categories.find { it.id == id } }
            if (selectedCategory != null) {
                FlashcardDetailPane(
                    category = selectedCategory,
                    flashcardRepository = flashcardRepository,
                    activity = activity,
                    fragmentActivity = context as? FragmentActivity,
                    onDataChanged = { refresh() },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            } else {
                Box(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.category_empty_text),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    } else {
        // Phone: category list, clicking launches FlashcardsActivity
        CategoryListPane(
            categories = categories,
            flashcardRepository = flashcardRepository,
            selectedCategoryId = null,
            onCategoryClick = { cat ->
                CategoryManagerSingleton.currentCategoryId = cat.id
                context.startActivity(Intent(context, FlashcardsActivity::class.java))
                activity?.let {
                    @Suppress("DEPRECATION")
                    it.overridePendingTransition(R.anim.right_in, R.anim.left_out)
                }
            },
            onAddCategory = {
                if (activity != null) {
                    AddCategoryDialog(activity).show().setOnDismissListener { refresh() }
                }
            },
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CategoryListPane(
    categories: List<Category>,
    flashcardRepository: FlashcardRepository,
    selectedCategoryId: Int?,
    onCategoryClick: (Category) -> Unit,
    onAddCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_categories_header),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onAddCategory) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.category_positive_btn_text))
            }
        }

        if (categories.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.category_empty_text),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    val isSelected = category.id == selectedCategoryId
                    val count = remember(category.id) {
                        flashcardRepository.getFlashcardsByCategoryID(category.id).size
                    }
                    CategoryCard(
                        category = category,
                        cardCount = count,
                        isSelected = isSelected,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    cardCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = category.getCategory(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val langFrom = category.getLangFrom()
            val langOn = category.getLangOn()
            val langText = if (langFrom.isNullOrEmpty() && langOn.isNullOrEmpty()) {
                stringResource(R.string.category_no_lang)
            } else if (langFrom.isNullOrEmpty() || langOn.isNullOrEmpty()) {
                stringResource(R.string.category_no_lang)
            } else {
                stringResource(R.string.category_lang_pair, langFrom, langOn)
            }
            Text(
                text = langText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.category_card_count, cardCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FlashcardDetailPane(
    category: Category,
    flashcardRepository: FlashcardRepository,
    activity: Activity?,
    fragmentActivity: FragmentActivity?,
    onDataChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Embed FlashcardsActivity content via AndroidView wrapping the existing Fragment/Activity pattern
    // For tablet split-view, we embed the FlashcardsActivity layout via a FragmentContainerView
    // containing a FlashcardDetailFragment.
    // For now, use a simpler approach: set the CategoryManagerSingleton and embed a FragmentContainerView
    // that hosts the FlashcardsActivity content.

    // Simplest approach: set CategoryManagerSingleton and use AndroidView with FlashcardsActivity's fragment
    CategoryManagerSingleton.currentCategoryId = category.id

    val containerId = remember(category.id) { View.generateViewId() }

    if (fragmentActivity != null) {
        AndroidView(
            factory = { ctx ->
                FragmentContainerView(ctx).apply {
                    id = containerId
                }
            },
            modifier = modifier,
            update = { view ->
                // Ensure view ID matches what we expect
                if (view.id != containerId) {
                    view.id = containerId
                }
                val fm = fragmentActivity.supportFragmentManager
                val tag = "flashcard_detail_${category.id}"
                if (fm.findFragmentByTag(tag) == null) {
                    // Remove old fragments in this container
                    fm.findFragmentById(containerId)?.let { old ->
                        fm.beginTransaction().remove(old).commitNowAllowingStateLoss()
                    }
                    fm.beginTransaction()
                        .replace(containerId, FlashcardDetailFragment.newInstance(category.id), tag)
                        .commitNowAllowingStateLoss()
                }
            }
        )
    }
}

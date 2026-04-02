package click.quickclicker.fiszki.activity.myWords

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.findCategoryColor
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.activity.myWords.category.CreateSetActivity
import click.quickclicker.fiszki.activity.myWords.category.EditSetActivity
import click.quickclicker.fiszki.activity.myWords.flashcards.AddFlashcardActivity
import click.quickclicker.fiszki.activity.myWords.flashcards.EditFlashcardActivity
import click.quickclicker.fiszki.activity.myWords.flashcards.FlashcardsActivity
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.LocalSharedPreferences
import click.quickclicker.fiszki.algorithm.fsrs.FsrsScheduler
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

@Composable
fun CategoryTabScreen(
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categoryRepository = remember { CategoryRepository(context) }
    val flashcardRepository = remember { FlashcardRepository(context) }
    val prefs = remember { LocalSharedPreferences(context) }
    val useFsrs = prefs.useFsrsAlgorithm

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

    var selectedCategoryId by rememberSaveable { mutableStateOf<Int?>(null) }

    if (isTablet && selectedCategoryId == null && categories.isNotEmpty()) {
        selectedCategoryId = categories[0].id
    }

    fun refresh() {
        refreshTrigger++
    }

    // Refresh when returning from CreateSetActivity or other activities
    @Suppress("DEPRECATION")
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (isTablet) {
        // Auto-clear selection if the selected category was deleted
        if (selectedCategoryId != null && categories.none { it.id == selectedCategoryId }) {
            selectedCategoryId = categories.firstOrNull()?.id
        }

        Row(modifier = modifier.fillMaxSize()) {
            CategoryListPane(
                categories = categories,
                flashcardRepository = flashcardRepository,
                useFsrs = useFsrs,
                selectedCategoryId = selectedCategoryId,
                onCategoryClick = { selectedCategoryId = it.id },
                onAddCategory = {
                    context.startActivity(Intent(context, CreateSetActivity::class.java))
                },
                refreshTrigger = refreshTrigger,
                modifier = Modifier.width(320.dp).fillMaxHeight()
            )
            VerticalDivider()
            val selectedCategory = selectedCategoryId?.let { id -> categories.find { it.id == id } }
            if (selectedCategory != null) {
                FlashcardDetailPaneCompose(
                    category = selectedCategory,
                    flashcardRepository = flashcardRepository,
                    useFsrs = useFsrs,
                    refreshTrigger = refreshTrigger,
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
        CategoryListPane(
            categories = categories,
            flashcardRepository = flashcardRepository,
            useFsrs = useFsrs,
            selectedCategoryId = null,
            onCategoryClick = { cat ->
                CategoryManagerSingleton.currentCategoryId = cat.id
                context.startActivity(Intent(context, FlashcardsActivity::class.java))
            },
            onAddCategory = {
                context.startActivity(Intent(context, CreateSetActivity::class.java))
            },
            refreshTrigger = refreshTrigger,
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CategoryListPane(
    categories: List<Category>,
    flashcardRepository: FlashcardRepository,
    useFsrs: Boolean,
    selectedCategoryId: Int?,
    onCategoryClick: (Category) -> Unit,
    onAddCategory: () -> Unit,
    refreshTrigger: Int = 0,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCategory,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.category_positive_btn_text))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(top = padding.calculateTopPadding())) {
            // Editorial header
            Column(
                modifier = Modifier.padding(start = 24.dp, top = 32.dp, end = 24.dp, bottom = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_categories_header).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = RobotoSerifFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 12.dp,
                        bottom = padding.calculateBottomPadding() + 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories, key = { it.id }) { category ->
                        val isSelected = category.id == selectedCategoryId
                        val flashcards = remember(refreshTrigger, category.id) {
                            flashcardRepository.getFlashcardsByCategoryID(category.id)
                        }
                        val count = flashcards.size
                        val mastery = remember(refreshTrigger, category.id) {
                            computeMastery(flashcards, useFsrs)
                        }
                        CategoryCard(
                            category = category,
                            cardCount = count,
                            masteryPercent = mastery,
                            isSelected = isSelected,
                            onClick = { onCategoryClick(category) }
                        )
                    }
                }
            }
        }
    }
}

private fun computeMastery(flashcards: List<Flashcard>, useFsrs: Boolean): Int {
    if (flashcards.isEmpty()) return 0
    if (useFsrs) {
        val scheduler = FsrsScheduler()
        val now = java.util.Date()
        val totalRetrievability = flashcards.sumOf { card ->
            scheduler.retrievability(card.toFsrsCard(), now)
        }
        return ((totalRetrievability / flashcards.size) * 100).toInt()
    }
    val totalAttempts = flashcards.sumOf { it.staticPass + it.staticFail }
    if (totalAttempts == 0) return 0
    val totalPass = flashcards.sumOf { it.staticPass }
    return ((totalPass.toFloat() / totalAttempts) * 100).toInt()
}

@Composable
private fun CategoryCard(
    category: Category,
    cardCount: Int,
    masteryPercent: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    Box {
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Row: set name + word count pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.getCategory(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = RobotoSerifFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = stringResource(R.string.category_word_count, cardCount),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                // Language pair subtitle
                val langFrom = category.getLangFrom()
                val langOn = category.getLangOn()
                if (!langFrom.isNullOrEmpty() && !langOn.isNullOrEmpty()) {
                    Text(
                        text = stringResource(R.string.category_lang_pair, langFrom, langOn),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mastery row: label + percentage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.category_mastery_label),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = stringResource(R.string.category_mastery_percent, masteryPercent),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Mastery progress bar
                LinearProgressIndicator(
                    progress = { masteryPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
            }
        }

        // Selected accent bar
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun FlashcardDetailPaneCompose(
    category: Category,
    flashcardRepository: FlashcardRepository,
    useFsrs: Boolean,
    refreshTrigger: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val flashcards = remember(refreshTrigger, category.id) {
        flashcardRepository.getFlashcardsByCategoryID(category.id)
    }

    val catColor = remember(category.id) {
        val cc = findCategoryColor(category.getColor()) ?: defaultCategoryColor()
        Color(cc.primary or 0xFF000000.toInt())
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    context.startActivity(
                        Intent(context, AddFlashcardActivity::class.java)
                            .putExtra(AddFlashcardActivity.EXTRA_CATEGORY_ID, category.id)
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add_new_card))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header: "Set Words List" headline
            Text(
                text = stringResource(R.string.detail_pane_header),
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = RobotoSerifFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Word count pill + Edit Collection button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = stringResource(R.string.category_word_count, flashcards.size),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                OutlinedButton(
                    onClick = {
                        context.startActivity(
                            Intent(context, EditSetActivity::class.java)
                                .putExtra(EditSetActivity.EXTRA_CATEGORY_ID, category.id)
                        )
                    },
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.action_edit_category))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (flashcards.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.flashcard_empty_text),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        bottom = padding.calculateBottomPadding() + 80.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(flashcards, key = { it.id }) { flashcard ->
                        WordCard(
                            flashcard = flashcard,
                            categoryColor = catColor,
                            useFsrs = useFsrs,
                            onClick = {
                                context.startActivity(
                                    Intent(context, EditFlashcardActivity::class.java)
                                        .putExtra(EditFlashcardActivity.EXTRA_FLASHCARD_ID, flashcard.id)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WordCard(
    flashcard: Flashcard,
    categoryColor: Color,
    useFsrs: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            Column {
                // Word
                Text(
                    text = flashcard.getWord(),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = RobotoSerifFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Small divider line
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Translation
                Text(
                    text = flashcard.getTranslation(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // FSRS / Priority indicator at bottom-end
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                if (useFsrs) {
                    DetailFsrsStateIndicator(
                        lastRating = flashcard.fsrsLastRating,
                        filledColor = categoryColor
                    )
                } else {
                    DetailPriorityIndicator(
                        priority = flashcard.priority,
                        filledColor = categoryColor
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailPriorityIndicator(priority: Int, filledColor: Color) {
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
private fun DetailFsrsStateIndicator(lastRating: Int, filledColor: Color) {
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

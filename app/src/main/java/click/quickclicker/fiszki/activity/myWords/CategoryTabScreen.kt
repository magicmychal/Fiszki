package click.quickclicker.fiszki.activity.myWords

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.activity.myWords.category.CreateSetActivity
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
    val activity = context as? Activity
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
                FlashcardDetailPane(
                    category = selectedCategory,
                    flashcardRepository = flashcardRepository,
                    activity = activity,
                    fragmentActivity = context as? FragmentActivity,
                    onDataChanged = { refresh() },
                    onCategoryDeleted = {
                        selectedCategoryId = null
                        refresh()
                    },
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
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
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
}

@Composable
private fun FlashcardDetailPane(
    category: Category,
    flashcardRepository: FlashcardRepository,
    activity: Activity?,
    fragmentActivity: FragmentActivity?,
    onDataChanged: () -> Unit,
    onCategoryDeleted: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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
                if (view.id != containerId) {
                    view.id = containerId
                }
                val fm = fragmentActivity.supportFragmentManager
                val tag = "flashcard_detail_${category.id}"
                val existing = fm.findFragmentByTag(tag)
                if (existing == null || !existing.isAdded || existing.view?.parent == null) {
                    existing?.let { fm.beginTransaction().remove(it).commitNowAllowingStateLoss() }
                    fm.findFragmentById(containerId)?.let { old ->
                        fm.beginTransaction().remove(old).commitNowAllowingStateLoss()
                    }
                    val fragment = FlashcardDetailFragment.newInstance(category.id)
                    fragment.onCategoryDeleted = onCategoryDeleted
                    fm.beginTransaction()
                        .replace(containerId, fragment, tag)
                        .commitNowAllowingStateLoss()
                } else {
                    (existing as? FlashcardDetailFragment)?.onCategoryDeleted = onCategoryDeleted
                }
            }
        )

        DisposableEffect(category.id) {
            onDispose {
                val fm = fragmentActivity.supportFragmentManager
                fm.findFragmentById(containerId)?.let { fragment ->
                    fm.beginTransaction().remove(fragment).commitNowAllowingStateLoss()
                }
            }
        }
    }
}

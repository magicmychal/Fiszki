package click.quickclicker.fiszki.activity.myWords.flashcards

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.LocalSharedPreferences
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.activity.ChangeActivityManager
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.activity.defaultCategoryColor
import click.quickclicker.fiszki.activity.findCategoryColor
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.activity.myWords.CategoryManagerSingleton
import click.quickclicker.fiszki.activity.myWords.category.EditSetActivity
import android.content.Intent
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FlashcardsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)

        setContent {
            FiszkiTheme {
                FlashcardsScreen(
                    activity = this,
                    onBack = { onBackPressedDispatcher.onBackPressed() },
                    onEditCategory = { categoryId ->
                        startActivity(
                            android.content.Intent(this, EditSetActivity::class.java)
                                .putExtra(EditSetActivity.EXTRA_CATEGORY_ID, categoryId)
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlashcardsScreen(
    activity: Activity,
    onBack: () -> Unit,
    onEditCategory: (Int) -> Unit
) {
    val context = LocalContext.current
    val categoryRepository = remember { CategoryRepository(context) }
    val flashcardRepository = remember { FlashcardRepository(context) }
    val prefs = remember { LocalSharedPreferences(context) }
    val useFsrs = prefs.useFsrsAlgorithm

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val deletedMessage = stringResource(R.string.snackbar_return_word_message)
    val undoLabel = stringResource(R.string.snackbar_return_word_button)

    var refreshTrigger by rememberSaveable { mutableIntStateOf(0) }

    // Refresh when returning from another activity (e.g. edit set, add flashcard)
    @Suppress("DEPRECATION")
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val category = remember(refreshTrigger) {
        categoryRepository.getCategoryByID(CategoryManagerSingleton.currentCategoryId)
    }

    if (category == null) {
        LaunchedEffect(Unit) { (context as? Activity)?.finish() }
        return
    }

    // Mutable state list for optimistic UI updates (undo support)
    val flashcards = remember { mutableStateListOf<Flashcard>() }
    LaunchedEffect(refreshTrigger) {
        flashcards.clear()
        flashcards.addAll(flashcardRepository.getFlashcardsByCategoryID(category.id))
    }

    val catColor = remember(category.getColor()) {
        findCategoryColor(category.getColor()) ?: defaultCategoryColor()
    }

    val catContainerColor = Color(catColor.container or 0xFF000000.toInt())
    val catPrimaryColor = Color(catColor.primary or 0xFF000000.toInt())

    val listState = rememberLazyListState()
    val density = LocalDensity.current

    // Track header height for collapse fraction calculation
    var headerHeightPx by remember { mutableStateOf(0f) }

    // Collapse fraction: 0f = header fully visible, 1f = header scrolled away
    val collapseFraction by remember {
        derivedStateOf {
            if (headerHeightPx <= 0f) return@derivedStateOf 0f
            when {
                listState.firstVisibleItemIndex > 0 -> 1f
                listState.firstVisibleItemIndex == 0 ->
                    (listState.firstVisibleItemScrollOffset / headerHeightPx).coerceIn(0f, 1f)
                else -> 0f
            }
        }
    }

    // Scrollbar: auto-hide after inactivity
    val isScrollInProgress by remember { derivedStateOf { listState.isScrollInProgress } }
    var isDraggingScrollbar by remember { mutableStateOf(false) }
    var showScrollbar by remember { mutableStateOf(false) }

    LaunchedEffect(isScrollInProgress, isDraggingScrollbar) {
        if (isScrollInProgress || isDraggingScrollbar) {
            showScrollbar = true
        } else {
            delay(1500L)
            showScrollbar = false
        }
    }

    val scrollbarAlpha by animateFloatAsState(
        targetValue = if (showScrollbar) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "scrollbarAlpha"
    )

    val categoryName = category.getCategory() ?: ""

    // Total items in LazyColumn: 1 (header) + 1 (empty state or 0) + flashcards.size
    val totalItemCount by remember {
        derivedStateOf { listState.layoutInfo.totalItemsCount }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = RobotoSerifFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.alpha(collapseFraction)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            FloatingActionButton(
                onClick = {
                    context.startActivity(
                        Intent(context, AddFlashcardActivity::class.java)
                            .putExtra(AddFlashcardActivity.EXTRA_CATEGORY_ID, category.id)
                    )
                },
                modifier = Modifier.padding(bottom = navBarPadding),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add_new_card))
            }
        }
    ) { padding ->
        val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val scrollbarColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        val scrollbarWidthPx = with(density) { 4.dp.toPx() }
        val scrollbarCornerPx = with(density) { 4.dp.toPx() }
        val scrollbarMarginPx = with(density) { 4.dp.toPx() }
        val minThumbHeightPx = with(density) { 40.dp.toPx() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp + navBarPadding)
            ) {
                // Editorial header
                item {
                    val headerAlpha = 1f - collapseFraction
                    Column(
                        modifier = Modifier
                            .padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 16.dp)
                            .onGloballyPositioned { coordinates ->
                                headerHeightPx = coordinates.size.height.toFloat()
                            }
                            .alpha(headerAlpha)
                    ) {
                        Text(
                            text = stringResource(R.string.flashcard_vocabulary_deck_label).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.headlineLarge,
                            fontFamily = RobotoSerifFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Edit Collection chip
                        Surface(
                            onClick = { onEditCategory(category.id) },
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.action_edit_category),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.flashcard_collection_count, flashcards.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (flashcards.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.flashcard_empty_text),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    itemsIndexed(flashcards, key = { _, f -> f.id }) { _, flashcard ->
                        SwipeToDeleteItem(
                            flashcard = flashcard,
                            onDelete = {
                                val deletedFlashcard = flashcard
                                val deletedIndex = flashcards.indexOfFirst { it.id == flashcard.id }
                                    .coerceAtLeast(0)
                                flashcardRepository.deleteFlashcard(flashcard)
                                flashcards.removeAll { it.id == flashcard.id }

                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = deletedMessage,
                                        actionLabel = undoLabel,
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        flashcardRepository.addFlashcard(deletedFlashcard)
                                        flashcards.add(
                                            deletedIndex.coerceIn(0, flashcards.size),
                                            deletedFlashcard
                                        )
                                    }
                                }
                            },
                            onEdit = {
                                context.startActivity(
                                    Intent(context, EditFlashcardActivity::class.java)
                                        .putExtra(EditFlashcardActivity.EXTRA_FLASHCARD_ID, flashcard.id)
                                )
                            },
                            catColorPrimary = catColor.primary,
                            useFsrs = useFsrs
                        )
                    }
                }
            }

            // Draggable scrollbar
            if (totalItemCount > 2 && scrollbarAlpha > 0f) {
                val layoutInfo = listState.layoutInfo
                val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
                if (viewportHeight > 0 && totalItemCount > 0) {
                    val thumbFraction = (layoutInfo.visibleItemsInfo.size.toFloat() / totalItemCount)
                        .coerceIn(0.05f, 1f)
                    val thumbHeightPx = (viewportHeight * thumbFraction).coerceAtLeast(minThumbHeightPx)
                    val trackHeightPx = viewportHeight.toFloat()

                    // Scroll position fraction
                    val scrollFraction by remember {
                        derivedStateOf {
                            val info = listState.layoutInfo
                            val totalItems = info.totalItemsCount
                            if (totalItems <= 1) return@derivedStateOf 0f
                            val firstVisible = info.visibleItemsInfo.firstOrNull()
                                ?: return@derivedStateOf 0f
                            val itemHeight = firstVisible.size.coerceAtLeast(1)
                            val scrolled = firstVisible.index.toFloat() +
                                    (-firstVisible.offset.toFloat() / itemHeight)
                            (scrolled / (totalItems - 1)).coerceIn(0f, 1f)
                        }
                    }

                    val thumbOffset = scrollFraction * (trackHeightPx - thumbHeightPx)

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .fillMaxHeight()
                            .width(with(density) { (scrollbarWidthPx + scrollbarMarginPx * 2).toDp() })
                            .alpha(scrollbarAlpha)
                            .pointerInput(totalItemCount) {
                                detectVerticalDragGestures(
                                    onDragStart = { isDraggingScrollbar = true },
                                    onDragEnd = { isDraggingScrollbar = false },
                                    onDragCancel = { isDraggingScrollbar = false },
                                    onVerticalDrag = { change, _ ->
                                        change.consume()
                                        val y = change.position.y
                                        val fraction = (y / trackHeightPx).coerceIn(0f, 1f)
                                        val targetIndex = (fraction * (totalItemCount - 1))
                                            .toInt()
                                            .coerceIn(0, totalItemCount - 1)
                                        coroutineScope.launch {
                                            listState.scrollToItem(targetIndex)
                                        }
                                    }
                                )
                            }
                            .drawWithContent {
                                drawContent()
                                drawRoundRect(
                                    color = scrollbarColor,
                                    topLeft = Offset(
                                        x = size.width - scrollbarWidthPx - scrollbarMarginPx,
                                        y = thumbOffset
                                    ),
                                    size = Size(scrollbarWidthPx, thumbHeightPx),
                                    cornerRadius = CornerRadius(scrollbarCornerPx, scrollbarCornerPx)
                                )
                            }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteItem(
    flashcard: Flashcard,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    catColorPrimary: Int,
    useFsrs: Boolean
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    // Reset dismiss state when item re-enters composition after undo
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    val isSwiping = dismissState.targetValue != SwipeToDismissBoxValue.Settled

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isSwiping) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.background
                    )
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (isSwiping) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        FlashcardListItemInline(
            word = flashcard.getWord(),
            translation = flashcard.getTranslation(),
            priority = flashcard.priority,
            categoryColor = Color(catColorPrimary or 0xFF000000.toInt()),
            useFsrs = useFsrs,
            lastRating = flashcard.fsrsLastRating,
            onClick = onEdit
        )
    }
}

@Composable
private fun FlashcardListItemInline(
    word: String,
    translation: String,
    priority: Int,
    categoryColor: Color,
    useFsrs: Boolean,
    lastRating: Int,
    onClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = RobotoSerifFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.35f, fill = false)
            )
            Text(
                text = "\u2192",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                text = translation,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.45f)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

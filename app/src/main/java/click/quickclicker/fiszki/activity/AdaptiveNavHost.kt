package click.quickclicker.fiszki.activity

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.exam.ExamSetupScreen
import click.quickclicker.fiszki.activity.exam.RoundsOption
import click.quickclicker.fiszki.activity.learning.PracticeCategoryItem
import click.quickclicker.fiszki.activity.learning.PracticeSetupScreen
import click.quickclicker.fiszki.activity.myWords.CategoryTabScreen
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

enum class NavTab { FLASHCARDS, LEARNING, EXAM, SETTINGS }

data class NavItem(
    val tab: NavTab,
    val labelRes: Int,
    val icon: ImageVector
)

private val navItems = listOf(
    NavItem(NavTab.FLASHCARDS, R.string.nav_flashcards, Icons.Default.ContentCopy),
    NavItem(NavTab.LEARNING, R.string.nav_learning_mode, Icons.Default.School),
    NavItem(NavTab.EXAM, R.string.nav_exam, Icons.Default.Quiz),
    NavItem(NavTab.SETTINGS, R.string.nav_settings, Icons.Default.Settings)
)

@Composable
fun AdaptiveNavHost(initialTab: NavTab = NavTab.FLASHCARDS) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val useRail = windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }

    if (useRail) {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                navItems.forEach { item ->
                    NavigationRailItem(
                        selected = selectedTab == item.tab,
                        onClick = { selectedTab = item.tab },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(stringResource(item.labelRes)) }
                    )
                }
            }
            VerticalDivider()
            TabContent(
                selectedTab = selectedTab,
                isTablet = true,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = selectedTab == item.tab,
                            onClick = { selectedTab = item.tab },
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(stringResource(item.labelRes)) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            TabContent(
                selectedTab = selectedTab,
                isTablet = false,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun TabContent(
    selectedTab: NavTab,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    when (selectedTab) {
        NavTab.FLASHCARDS -> {
            CategoryTabScreen(
                isTablet = isTablet,
                modifier = modifier
            )
        }
        NavTab.LEARNING -> {
            val categoryRepository = CategoryRepository(context)
            val flashcardRepository = FlashcardRepository(context)
            val allCategories = categoryRepository.getAllCategory()
            val categoryItems = buildList {
                add(
                    PracticeCategoryItem(
                        id = null,
                        displayName = context.getString(R.string.learning_category_all),
                        langFrom = null,
                        langOn = null
                    )
                )
                allCategories.forEach { cat ->
                    add(
                        PracticeCategoryItem(
                            id = cat.id,
                            displayName = cat.getCategory(),
                            langFrom = cat.getLangFrom(),
                            langOn = cat.getLangOn()
                        )
                    )
                }
            }

            val contentModifier = if (isTablet) {
                Modifier.widthIn(max = 500.dp)
            } else {
                modifier
            }
            val wrapModifier = if (isTablet) modifier else Modifier
            Box(
                modifier = wrapModifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                PracticeSetupScreen(
                    title = context.getString(R.string.learning_title),
                    categories = categoryItems,
                    onStartPractice = { strictMode, categoryId, reversed ->
                        val flashcards = if (categoryId == null) {
                            flashcardRepository.getAllFlashcards()
                        } else {
                            flashcardRepository.getFlashcardsByCategoryID(categoryId)
                        }
                        if (flashcards.isEmpty()) {
                            Toast.makeText(context, R.string.learning_no_flashcards, Toast.LENGTH_LONG).show()
                        } else if (activity != null) {
                            ChangeActivityManager(activity).goToLearningCheck(
                                flashcards = flashcards,
                                strictMode = strictMode,
                                reversed = reversed
                            )
                        }
                    },
                    modifier = contentModifier
                )
            }
        }
        NavTab.EXAM -> {
            val categoryRepository = CategoryRepository(context)
            val flashcardRepository = FlashcardRepository(context)
            val allCategories = categoryRepository.getAllCategory()
            val categoryItems = buildList {
                add(
                    PracticeCategoryItem(
                        id = null,
                        displayName = context.getString(R.string.learning_category_all),
                        langFrom = null,
                        langOn = null
                    )
                )
                allCategories.forEach { cat ->
                    add(
                        PracticeCategoryItem(
                            id = cat.id,
                            displayName = cat.getCategory(),
                            langFrom = cat.getLangFrom(),
                            langOn = cat.getLangOn()
                        )
                    )
                }
            }
            val roundsOptions = listOf(5, 10, 15, 25, 50).map {
                RoundsOption(value = it, label = it.toString())
            }

            val contentModifier = if (isTablet) {
                Modifier.widthIn(max = 500.dp)
            } else {
                modifier
            }
            val wrapModifier = if (isTablet) modifier else Modifier
            Box(
                modifier = wrapModifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                ExamSetupScreen(
                    title = context.getString(R.string.exam_title),
                    categories = categoryItems,
                    roundsOptions = roundsOptions,
                    onStartExam = { strictMode, categoryId, reversed, rounds ->
                        val flashcards = if (categoryId == null) {
                            flashcardRepository.getAllFlashcards()
                        } else {
                            flashcardRepository.getFlashcardsByCategoryID(categoryId)
                        }
                        if (flashcards.isEmpty()) {
                            Toast.makeText(context, R.string.exam_no_flashcards, Toast.LENGTH_LONG).show()
                        } else if (activity != null) {
                            val categoryName = if (categoryId == null) {
                                context.getString(R.string.learning_category_all)
                            } else {
                                categoryRepository.getCategoryByID(categoryId)?.getCategory()
                            }
                            val category = if (categoryId != null) categoryRepository.getCategoryByID(categoryId) else null
                            val languagePair = if (category != null && !category.getLangFrom().isNullOrEmpty() && !category.getLangOn().isNullOrEmpty()) {
                                val from = if (reversed) category.getLangOn() else category.getLangFrom()
                                val to = if (reversed) category.getLangFrom() else category.getLangOn()
                                "$from to $to"
                            } else null
                            ChangeActivityManager(activity).goToExamCheck(flashcards, rounds, categoryName, languagePair)
                        }
                    },
                    modifier = contentModifier
                )
            }
        }
        NavTab.SETTINGS -> {
            val fragmentActivity = context as? FragmentActivity
            if (fragmentActivity != null) {
                SettingsFragmentHost(
                    fragmentActivity = fragmentActivity,
                    modifier = modifier
                )
            }
        }
    }
}

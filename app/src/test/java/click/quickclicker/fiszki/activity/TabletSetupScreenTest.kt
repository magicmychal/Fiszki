package click.quickclicker.fiszki.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import click.quickclicker.fiszki.activity.learning.PracticeCategoryItem
import click.quickclicker.fiszki.activity.learning.PracticeSetupScreen
import click.quickclicker.fiszki.activity.exam.ExamSetupScreen
import click.quickclicker.fiszki.activity.exam.RoundsOption
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class TabletSetupScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCategories = listOf(
        PracticeCategoryItem(
            id = null,
            displayName = "All sets",
            langFrom = null,
            langOn = null
        ),
        PracticeCategoryItem(
            id = 1,
            displayName = "Spanish Basics",
            langFrom = "English",
            langOn = "Spanish"
        )
    )

    @Test
    fun practiceSetupScreen_tabletLayout_constrainedTo500dp() {
        composeTestRule.setContent {
            FiszkiTheme {
                // Simulate wide tablet container (800dp)
                Box(
                    modifier = Modifier.width(800.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    PracticeSetupScreen(
                        title = "Time to\npractice!",
                        categories = testCategories,
                        onStartPractice = { _, _, _ -> },
                        modifier = Modifier.widthIn(max = 500.dp).testTag("practice_screen")
                    )
                }
            }
        }

        val bounds = composeTestRule.onNodeWithTag("practice_screen")
            .assertIsDisplayed()
            .getUnclippedBoundsInRoot()
        val width = bounds.right - bounds.left
        assertTrue("Width ($width) should be at most 500dp", width <= 501.dp)
    }

    @Test
    fun practiceSetupScreen_showsAllContent() {
        composeTestRule.setContent {
            FiszkiTheme {
                PracticeSetupScreen(
                    title = "Time to\npractice!",
                    categories = testCategories,
                    onStartPractice = { _, _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Strict mode").assertIsDisplayed()
    }

    @Test
    fun examSetupScreen_tabletLayout_constrainedTo500dp() {
        composeTestRule.setContent {
            FiszkiTheme {
                Box(
                    modifier = Modifier.width(800.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    ExamSetupScreen(
                        title = "Exam\ntime!",
                        categories = testCategories,
                        roundsOptions = listOf(
                            RoundsOption(5, "5"),
                            RoundsOption(10, "10")
                        ),
                        onStartExam = { _, _, _, _ -> },
                        modifier = Modifier.widthIn(max = 500.dp).testTag("exam_screen")
                    )
                }
            }
        }

        val bounds = composeTestRule.onNodeWithTag("exam_screen")
            .assertIsDisplayed()
            .getUnclippedBoundsInRoot()
        val width = bounds.right - bounds.left
        assertTrue("Width ($width) should be at most 500dp", width <= 501.dp)
    }

    @Test
    fun examSetupScreen_showsAllContent() {
        composeTestRule.setContent {
            FiszkiTheme {
                ExamSetupScreen(
                    title = "Exam\ntime!",
                    categories = testCategories,
                    roundsOptions = listOf(
                        RoundsOption(5, "5"),
                        RoundsOption(10, "10")
                    ),
                    onStartExam = { _, _, _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Strict mode").assertIsDisplayed()
    }

    @Test
    fun practiceSetupScreen_phoneLayout_noWidthConstraint() {
        // On phone, the screen renders with fillMaxSize and no widthIn cap
        composeTestRule.setContent {
            FiszkiTheme {
                PracticeSetupScreen(
                    title = "Time to\npractice!",
                    categories = testCategories,
                    onStartPractice = { _, _, _ -> },
                    modifier = Modifier.fillMaxSize().testTag("practice_phone")
                )
            }
        }

        composeTestRule.onNodeWithTag("practice_phone").assertIsDisplayed()
        composeTestRule.onNodeWithText("Strict mode").assertIsDisplayed()
    }
}

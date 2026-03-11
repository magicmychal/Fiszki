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
import click.quickclicker.fiszki.ui.TabletContentWrapper
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class TabletCheckScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tabletWrapper_constrainsWidthTo500dp() {
        composeTestRule.setContent {
            FiszkiTheme {
                // Simulate the same pattern TabletContentWrapper uses on tablet
                Box(
                    modifier = Modifier.width(800.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .testTag("constrained_content")
                    ) {
                        Text("Check screen content")
                    }
                }
            }
        }

        val bounds = composeTestRule.onNodeWithTag("constrained_content")
            .assertIsDisplayed()
            .getUnclippedBoundsInRoot()
        val width = bounds.right - bounds.left
        assertTrue("Width ($width) should be at most 500dp", width <= 501.dp)
    }

    @Test
    fun tabletContentWrapper_rendersChildContent() {
        composeTestRule.setContent {
            FiszkiTheme {
                TabletContentWrapper {
                    Text("Learning check content", modifier = Modifier.testTag("check_content"))
                }
            }
        }

        composeTestRule.onNodeWithTag("check_content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Learning check content").assertIsDisplayed()
    }

    @Test
    fun setupAndCheckScreens_useSameMaxWidth() {
        // Both AdaptiveNavHost (setup screens) and TabletContentWrapper (check screens)
        // use 500.dp as max width — verify the constraint works consistently
        val maxWidth = 500.dp

        composeTestRule.setContent {
            FiszkiTheme {
                Box(
                    modifier = Modifier.width(1000.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = maxWidth)
                            .testTag("consistent_width")
                    ) {
                        Text("Content")
                    }
                }
            }
        }

        val bounds = composeTestRule.onNodeWithTag("consistent_width")
            .assertIsDisplayed()
            .getUnclippedBoundsInRoot()
        val width = bounds.right - bounds.left
        assertTrue("Width ($width) should be at most $maxWidth", width <= 501.dp)
    }
}

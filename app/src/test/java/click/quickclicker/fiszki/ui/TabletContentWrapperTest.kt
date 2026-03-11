package click.quickclicker.fiszki.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import click.quickclicker.fiszki.activity.FiszkiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class TabletContentWrapperTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tabletContentWrapper_showsContent() {
        composeTestRule.setContent {
            FiszkiTheme {
                TabletContentWrapper {
                    Text("Test Content", modifier = Modifier.testTag("content"))
                }
            }
        }

        composeTestRule.onNodeWithText("Test Content").assertIsDisplayed()
    }

    @Test
    fun tabletContentWrapper_onPhone_contentRenderedDirectly() {
        // On phone-sized screens (default Robolectric), content is rendered without wrapper
        composeTestRule.setContent {
            FiszkiTheme {
                TabletContentWrapper {
                    Box(modifier = Modifier.fillMaxWidth().testTag("phone_content")) {
                        Text("Phone Content")
                    }
                }
            }
        }

        composeTestRule.onNodeWithTag("phone_content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Phone Content").assertIsDisplayed()
    }
}

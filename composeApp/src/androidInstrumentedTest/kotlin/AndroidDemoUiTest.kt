import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.my.drivexcel.platform.utils.phoneWindowSizeClassPreview
import org.my.drivexcel.theme.DriveXcelAppTheme
import org.my.drivexcel.v4.ui.screens.home.HomeRoute
import kotlin.test.Test

class AndroidDemoUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun startTest() {
        composeTestRule.setContent {
            DriveXcelAppTheme(
                windowSizeClass = phoneWindowSizeClassPreview
            ) {
                HomeRoute(onCreateEvent = { }, onUpdateEvent = { })
            }
        }

        composeTestRule.onNodeWithText("Main").assertExists()
    }
}
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.my.drivexcel.platform.phoneWindowSizeClassPreview
import org.my.drivexcel.ui.theme.DriveXcelAppTheme
import org.my.drivexcel.ui.screens.home.HomeRoute
import kotlin.test.Test

class AndroidDemoUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun startTest() {
        composeTestRule.setContent {
            DriveXcelAppTheme(
                myWindowSizeClass = phoneWindowSizeClassPreview
            ) {
                HomeRoute(onCreateEvent = { }, onUpdateEvent = { })
            }
        }

        composeTestRule.onNodeWithText("Main").assertExists()
    }
}
package org.my.drivexcel.integration.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import org.my.drivexcel.platform.phoneWindowSizeClassPreview
import org.my.drivexcel.v4.ui.theme.DriveXcelAppTheme
import org.my.drivexcel.v4.ui.screens.home.HomeRoute
import kotlin.test.Test

class SomeDemoUiTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myTest() = runComposeUiTest {


        setContent {
            DriveXcelAppTheme(
                myWindowSizeClass = phoneWindowSizeClassPreview
            ) {
                HomeRoute(onCreateEvent = { }, onUpdateEvent = { })
            }
        }

        onNodeWithText("Main").assertExists()
    }
}
package org.my.drivexcel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.my.drivexcel.navigation.AppScreens

@Composable
fun rememberDriveXcelAppState(
    navController: NavHostController = rememberNavController(),
    homeNavController: NavHostController = rememberNavController(),
    settingsNavController: NavHostController = rememberNavController(),
    addEventNavController: NavHostController = rememberNavController()
): DriveXcelAppState {
    return remember {
        DriveXcelAppState(
            navController = navController,
            homeNavController = homeNavController,
            settingsNavController = settingsNavController,
            addEventNavController = addEventNavController
        )
    }
}

@Stable
class DriveXcelAppState(
    val navController: NavHostController,
    val homeNavController: NavHostController,
    val settingsNavController: NavHostController,
    val addEventNavController: NavHostController
) {

    val currentTab = mutableStateOf(AppScreens.Home.route)

    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            // Collect the currentBackStackEntryFlow as a state
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            // Fallback to previousDestination if currentEntry is null
            val result = currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
            println(result?.route ?: "no route")
            return result
        }

    val currentBackStackEntryFlow = navController.currentBackStackEntryFlow

    fun navigate(route: String) {
        currentTab.value = route
    }

}
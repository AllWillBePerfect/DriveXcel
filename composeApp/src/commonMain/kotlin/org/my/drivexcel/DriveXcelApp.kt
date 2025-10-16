package org.my.drivexcel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.compose_multiplatform
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.navigation.AppScreens
import org.my.drivexcel.navigation.RailScreens
import org.my.drivexcel.platform.utils.WindowSize
import org.my.drivexcel.theme.LocalWindowSize
import org.my.drivexcel.ui.screens.home.HomeRoute
import org.my.drivexcel.ui.screens.settings.SettingsRoute

@Composable
@Preview
fun DriveXcelApp(
) {

    val scope = rememberCoroutineScope()

    val appState = rememberDriveXcelAppState()
    val wideNavigationRailState =
        rememberWideNavigationRailState(initialValue = WideNavigationRailValue.Collapsed)

    val windowSize = LocalWindowSize.current
    val isDrawerActive = windowSize == WindowSize.Compact

//    Scaffold { innerPadding ->
    Row(
//            modifier = Modifier.padding(innerPadding)
    ) {
        ModalWideNavigationRail(
            modifier = Modifier,
            state = wideNavigationRailState,
            colors = WideNavigationRailDefaults.colors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            hideOnCollapse = isDrawerActive,
            expandedShape = RoundedCornerShape(0.dp)
        ) {
            RailScreens.entries.forEachIndexed { index, screen ->

                NavigationRailItem(
                    selected = appState.currentTab.value == screen.navigateTo,
                    onClick = {
//                                appState.navController.navigate(route = screen.navigateTo)
                        appState.navigate(screen.navigateTo)
//                            selectedDestination = index
                    },
                    icon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(screen.image),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    },
                    label = { Text(screen.route) }
                )
            }
        }

        DriveXcelNavigation(
            appState = appState,
            openDrawer = { scope.launch { wideNavigationRailState.expand() } }
        )
    }
//    }


}


@Composable
private fun DriveXcelNavigation(
    appState: DriveXcelAppState,
    openDrawer: () -> Unit
) {

    val windowSize = LocalWindowSize.current
    val currentDestination = appState.currentDestination
    LaunchedEffect(windowSize) {

        when (windowSize) {
            WindowSize.Compact -> {
                if (currentDestination?.route == AppScreens.HomeWithDetails.route) {

                }
            }

            WindowSize.Medium -> {}
            WindowSize.Expanded -> {}
        }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            appState.currentBackStackEntryFlow.collect {
                print(it.destination.route)
            }
        }
    }

    when (appState.currentTab.value) {
        AppScreens.HomeWithDetails.route -> {
            NavHost(
                navController = appState.homeNavController,
                startDestination = AppScreens.HomeWithDetails.route
            ) {

                composable(route = AppScreens.HomeWithDetails.route) {
                    HomeRoute(
                        openDrawer = openDrawer
                    )
                }

            }
        }

        AppScreens.Settings.route -> {
            NavHost(
                navController = appState.settingsNavController,
                startDestination = AppScreens.Settings.route
            ) {
                composable(route = AppScreens.Settings.route) {
                    SettingsRoute(
                        onBackPressed = {
                            appState.navigate(AppScreens.HomeWithDetails.route)
                        }
                    )
                }

            }
        }
    }

    /*NavHost(
        navController = appState.navController,
        startDestination = AppScreens.Settings.route
    ) {

        composable(route = AppScreens.HomeWithDetails.route) {
            HomeRoute()
        }

        composable(route = AppScreens.Settings.route) {
            SettingsRoute()
        }

    }*/
}

@Composable
private fun ExpandableIconScreen() {
    var showContent by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!")
        }
        AnimatedVisibility(showContent) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
            }
        }
    }
}
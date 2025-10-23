package org.my.drivexcel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailColors
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_create_folder
import drivexcel.composeapp.generated.resources.ic_menu
import drivexcel.composeapp.generated.resources.ic_menu_open
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.my.drivexcel.navigation.AppScreens
import org.my.drivexcel.navigation.RailScreens
import org.my.drivexcel.platform.utils.WindowSize
import org.my.drivexcel.theme.LocalWindowSize
import org.my.drivexcel.ui.screens.addevent.AddEventRoute
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
    val isCompact = windowSize == WindowSize.Compact

    LaunchedEffect(windowSize) {
        scope.launch {
            when (windowSize) {
                WindowSize.Compact -> {}
                WindowSize.Medium -> {
                    wideNavigationRailState.collapse()
                }

                WindowSize.Expanded -> {
                    wideNavigationRailState.expand()
                }
            }
        }
    }

    Scaffold { innerPadding ->
        Row(
        ) {
            NavigationRailContainer(
                isCompat = isCompact,
                modifier = Modifier,
                state = wideNavigationRailState,
                colors = WideNavigationRailDefaults.colors(
//                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                hideOnCollapse = isCompact,
                expandedShape = RoundedCornerShape(0.dp),
                header = {
                    Column(modifier = Modifier.padding(start = 20.dp)){
                        if (isCompact) {
                            IconButton(onClick = {
                                if (wideNavigationRailState.currentValue == WideNavigationRailValue.Collapsed) {
                                    scope.launch { wideNavigationRailState.expand() }
                                } else {
                                    scope.launch { wideNavigationRailState.collapse() }
                                }
                            }) {
                                Icon(
                                    painter = painterResource(if (wideNavigationRailState.currentValue == WideNavigationRailValue.Collapsed) Res.drawable.ic_menu else Res.drawable.ic_menu_open),
                                    contentDescription = null
                                )
                            }
                        }

                        ExtendedFloatingActionButton(
                            expanded = wideNavigationRailState.currentValue == WideNavigationRailValue.Expanded,
                            onClick = {
                                appState.navigate(AppScreens.AddEvent.route)
                                if (isCompact) scope.launch { wideNavigationRailState.collapse() }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_create_folder),
                                    contentDescription = null
                                )
                            },
                            text = {
                                Text("Add Event")
                            }
                        )
                    }
                },
                content = {
                    RailScreens.entries.forEachIndexed { index, screen ->
                        WideNavigationRailItem(
                            selected = appState.currentTab.value == screen.navigateTo,
                            onClick = {
//                                appState.navController.navigate(route = screen.navigateTo)
                                if (isCompact) {
                                    val currentRoute = appState.currentTab.value
                                    if (currentRoute != screen.navigateTo) scope.launch { wideNavigationRailState.collapse() }
                                }
                                appState.navigate(screen.navigateTo)
//                            selectedDestination = index
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(screen.image),
                                    contentDescription = null,
                                )
                            },
                            label = { Text(screen.route) },
                            railExpanded = wideNavigationRailState.currentValue == WideNavigationRailValue.Expanded
                        )
                    }
                }
            )
            /*
            ModalWideNavigationRail(
                modifier = Modifier.padding(innerPadding),
                state = wideNavigationRailState,
                colors = WideNavigationRailDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer

                ),
                hideOnCollapse = isCompact,
                expandedShape = RoundedCornerShape(0.dp),
                header = {

                    Column {
                        IconButton(onClick = {
                            if (wideNavigationRailState.currentValue == WideNavigationRailValue.Collapsed) {
                                scope.launch { wideNavigationRailState.expand() }
                            } else {
                                scope.launch { wideNavigationRailState.collapse() }
                            }
                        }) {
                            Icon(
                                painter = painterResource(if (wideNavigationRailState.currentValue == WideNavigationRailValue.Collapsed) Res.drawable.ic_menu else Res.drawable.ic_menu_open),
                                contentDescription = null
                            )
                        }

                        ExtendedFloatingActionButton(onClick = {
                            appState.navigate(AppScreens.AddEvent.route)
                        }) {
                            Text("Add Event")
                        }
                    }
                }
            ) {
                RailScreens.entries.forEachIndexed { index, screen ->

                    WideNavigationRailItem(
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
                        label = { Text(screen.route) },
                        railExpanded = wideNavigationRailState.currentValue == WideNavigationRailValue.Expanded
                    )
                }
            }*/

            DriveXcelNavigation(
                appState = appState,
                openDrawer = { scope.launch { wideNavigationRailState.expand() } }
            )
        }
    }

}

@Composable
private fun NavigationRailContainer(
    isCompat: Boolean,
    modifier: Modifier,
    state: WideNavigationRailState,
    colors: WideNavigationRailColors,
    hideOnCollapse: Boolean = false,
    expandedShape: Shape,
    header: @Composable (() -> Unit),
    content: @Composable () -> Unit,
) {
    if (isCompat) {
        ModalNavigationRail(
            modifier = modifier,
            state = state,
            colors = colors,
            hideOnCollapse = hideOnCollapse,
            expandedShape = expandedShape,
            header = header,
            content = content
        )
    } else {
        ExpandedNavigationRail(
            modifier = modifier,
            state = state,
            colors = colors,
            header = header,
            content = content
        )
    }
}

@Composable
private fun ModalNavigationRail(
    modifier: Modifier = Modifier,
    state: WideNavigationRailState = rememberWideNavigationRailState(),
    colors: WideNavigationRailColors = WideNavigationRailDefaults.colors(),
    hideOnCollapse: Boolean = false,
    expandedShape: Shape = WideNavigationRailDefaults.modalExpandedShape,
    header: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
    ) {
    ModalWideNavigationRail(
        modifier = modifier,
        state = state,
        colors = colors,
        hideOnCollapse = hideOnCollapse,
        expandedShape = expandedShape,
        header = header,
        content = content
    )
}

@Composable
private fun ExpandedNavigationRail(
    modifier: Modifier = Modifier,
    state: WideNavigationRailState = rememberWideNavigationRailState(),
    colors: WideNavigationRailColors = WideNavigationRailDefaults.colors(),
    header: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    WideNavigationRail(
        modifier = modifier,
        state = state,
        colors = colors,
        header = header,
        content = content
    )
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
                if (currentDestination?.route == AppScreens.Home.route) {

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
        AppScreens.Home.route -> {
            NavHost(
                navController = appState.homeNavController,
                startDestination = AppScreens.Home.route
            ) {

                composable(route = AppScreens.Home.route) {
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
                            appState.navigate(AppScreens.Home.route)
                        }
                    )
                }

            }
        }

        AppScreens.AddEvent.route -> {
            NavHost(
                navController = appState.addEventNavController,
                startDestination = AppScreens.AddEvent.route
            ) {
                composable(route = AppScreens.AddEvent.route) {
                    AddEventRoute(
                        onBackPressed = {
                            appState.navigate(AppScreens.Home.route)
                        }
                    )
                }
            }
        }
    }
}
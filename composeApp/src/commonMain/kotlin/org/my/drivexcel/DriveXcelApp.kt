package org.my.drivexcel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_create_folder
import drivexcel.composeapp.generated.resources.ic_menu
import drivexcel.composeapp.generated.resources.ic_menu_open
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.navigation.AppScreens
import org.my.drivexcel.navigation.RailScreens
import org.my.drivexcel.platform.utils.WindowSize
import org.my.drivexcel.theme.LocalWindowSize
import org.my.drivexcel.ui.screens.addevent.AddEventRoute
import org.my.drivexcel.ui.screens.addevent.AddEventScreenFormat
import org.my.drivexcel.ui.screens.addevent.AddEventViewModel
import org.my.drivexcel.ui.screens.home.HomeRoute
import org.my.drivexcel.ui.screens.settings.SettingsRoute
import org.my.drivexcel.utils.ActionsManager

@Composable
@Preview
fun DriveXcelApp(

) {

    val actionsManager: ActionsManager = koinInject()

    val scope = rememberCoroutineScope()

    val appState = rememberDriveXcelAppState()
    val wideNavigationRailState =
        rememberWideNavigationRailState(initialValue = WideNavigationRailValue.Collapsed)

    val snackbarHostState = remember { SnackbarHostState() }

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

    LaunchedEffect(Unit) {
        actionsManager.snackbarActions.collect { action ->
            snackbarHostState.showSnackbar(action.message)

            /*when (action) {
                SnackbarActions.EventCreated -> {
                    snackbarHostState.showSnackbar(action.message)
                }
                is SnackbarActions.EventFailed -> {
                    snackbarHostState.showSnackbar(action.message)
                }
                is SnackbarActions.Info -> {
                    snackbarHostState.showSnackbar(action.message)
                }
            }*/

        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
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
                    Column(modifier = Modifier.padding(start = 20.dp)) {
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
                            selected = appState.currentTabRoute == screen.navigateTo,
                            onClick = {
//                                appState.navController.navigate(route = screen.navigateTo)
                                if (isCompact) {
                                    val currentRoute = appState.currentTabRoute
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
fun DriveXcelNavigation(appState: DriveXcelAppState, openDrawer: () -> Unit) {

    val addEventViewModel: AddEventViewModel = koinViewModel()


    val tabs = listOf(
        AppScreens.Home,
        AppScreens.Settings,
        AppScreens.AddEvent,
        AppScreens.EditEvent
    )

    // NavController для каждой вкладки
    val navControllers = remember {
        mapOf(
            AppScreens.Home to appState.homeNavController,
            AppScreens.Settings to appState.settingsNavController,
            AppScreens.AddEvent to appState.addEventNavController,
            AppScreens.EditEvent to appState.redactingNavController,
        )
    }

    val currentTab = appState.currentTabRoute

    Box(Modifier.fillMaxSize()) {
        tabs.forEach { tab ->
            val navController = navControllers[tab]!!
            val baseRoute = currentTab.substringBefore("/")
            val isSelected = tab.route.substringBefore("/") == baseRoute
            if (isSelected) {
                println("tab route: $currentTab")
                when (tab) {
                    AppScreens.Home -> HomeNavHost(
                        navController = navController,
                        openDrawer = openDrawer,
                        onEditEvent = { eventId ->
                            addEventViewModel.setPendingId(eventId)
                            appState.navigate(AppScreens.EditEvent.route)

                        }
                    )

                    AppScreens.Settings -> SettingsNavHost(
                        navController = navController,
                        onBackPressed = { appState.navigate(AppScreens.Home.route) }
                    )

                    AppScreens.AddEvent -> AddEventNavHost(
                        addEventViewModel = addEventViewModel,
                        navController = navController,
                        onBackPressed = { appState.navigate(AppScreens.Home.route) },
                    )

                    AppScreens.EditEvent -> EditEventNavHost(
                        addEventViewModel = addEventViewModel,
                        navController = navController,
                        onBackPressed = { appState.navigate(AppScreens.Home.route) },
                    )

                }
            }
        }
    }
}

// Home
@Composable
fun HomeNavHost(
    navController: NavHostController,
    openDrawer: () -> Unit,
    onEditEvent: (String) -> Unit
) {
    NavHost(navController, startDestination = AppScreens.Home.route) {
        composable(AppScreens.Home.route) {
            HomeRoute(
                openDrawer = openDrawer,
                onEventRedacting = onEditEvent
            )
        }
    }
}

// Settings
@Composable
fun SettingsNavHost(
    navController: NavHostController,
    onBackPressed: () -> Unit
) {
    NavHost(navController, startDestination = AppScreens.Settings.route) {
        composable(AppScreens.Settings.route) {
            SettingsRoute(onBackPressed = onBackPressed)
        }
    }
}

// AddEventNavHost — только создание
@Composable
fun AddEventNavHost(
    addEventViewModel: AddEventViewModel,
    navController: NavHostController,
    onBackPressed: () -> Unit,
) {
    NavHost(navController, startDestination = AppScreens.AddEvent.route) {

        composable(AppScreens.AddEvent.route) {
            val viewModel : AddEventViewModel = koinViewModel()
            AddEventRoute(
                viewModel = viewModel,
                onBackPressed = onBackPressed,
            )
        }

    }
}

// EditEventNavHost — только редактирование
@Composable
fun EditEventNavHost(
    addEventViewModel: AddEventViewModel,
    navController: NavHostController,
    onBackPressed: () -> Unit
) {
    print("i am here!")
    NavHost(navController, startDestination = AppScreens.EditEvent.route) {
        composable(
            route = AppScreens.EditEvent.route,
        ) {
            val id = addEventViewModel.getPendingId() ?: ""
            AddEventRoute(
                viewModel = addEventViewModel,
                format = AddEventScreenFormat.RedactingEvent(id),
                onBackPressed = onBackPressed
            )
        }
    }
}


// EditEventNavHost — только редактирование
/*@Composable
fun EditEventNavHost(
    navController: NavHostController,
    onBackPressed: () -> Unit
) {
    print("i am here!")
    NavHost(navController, startDestination = AppScreens.EditEvent.route) {
        composable(
            route = AppScreens.EditEvent.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.toRoute<EditEventArgs>().id
            AddEventRoute(
                format = AddEventScreenFormat.RedactingEvent(id),
                onBackPressed = onBackPressed
            )
        }
    }
}*/




/*@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DriveXcelNavigation(
    appState: DriveXcelAppState,
    openDrawer: () -> Unit
) {
    val currentTab = appState.currentTab.value
    var previousTab by remember { mutableStateOf(currentTab) }

    Box(Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = {
                if (targetState == AppScreens.Home.route && previousTab == AppScreens.AddEvent.route ||
                    targetState == AppScreens.Settings.route && previousTab == AppScreens.Home.route
                ) {
                    slideInVertically { -it } + fadeIn() with slideOutVertically { -it } + fadeOut()
                } else {
                    slideInVertically { it } + fadeIn() with slideOutVertically { -it } + fadeOut()
                }
            }
        ) { target ->
            previousTab = currentTab
            when (target) {
                AppScreens.Home.route -> NavHost(
                    navController = appState.homeNavController,
                    startDestination = AppScreens.Home.route
                ) {
                    composable(AppScreens.Home.route) {
                        HomeRoute(
                            onEventRedacting = { eventId ->
                                appState.addEventNavController.navigate(AppScreens.EditEvent.createRoute(eventId))
                            },
                            openDrawer = openDrawer
                        )
                    }
                }

                AppScreens.Settings.route -> NavHost(
                    navController = appState.settingsNavController,
                    startDestination = AppScreens.Settings.route
                ) {
                    composable(AppScreens.Settings.route) {
                        SettingsRoute(
                            onBackPressed = { appState.navigate(AppScreens.Home.route) }
                        )
                    }
                }

                AppScreens.AddEvent.route -> NavHost(
                    navController = appState.addEventNavController,
                    startDestination = AppScreens.AddEvent.route
                ) {
                    composable(AppScreens.AddEvent.route) {
                        AddEventRoute(
                            onBackPressed = { appState.navigate(AppScreens.Home.route) }
                        )
                    }
                    composable(
                        AppScreens.EditEvent.route,
                        arguments = listOf(navArgument("id") { type = NavType.StringType })
                    ) { entry ->
                        val args = entry.toRoute<EditEventArgs>()
                        AddEventRoute(
                            format = AddEventScreenFormat.RedactingEvent(args.id),
                            onBackPressed = { appState.navigate(AppScreens.Home.route) }
                        )
                    }
                }
            }
        }
    }
}*/


/*@Composable
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

    LaunchedEffect(appState.currentTab.value) {
        if (appState.currentTab.value == AppScreens.AddEvent.route) {
            val id = appState.pendingEditEventId
            if (id != null) {
                appState.addEventNavController.navigate(AppScreens.EditEvent.createRoute(id))
                appState.pendingEditEventId = null
            } else {
                appState.addEventNavController.navigate(AppScreens.AddEvent.route)
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
                        onEventRedacting = {
                            // Сохраняем id для редактирования
                            appState.pendingEditEventId = it

                            // Переключаем вкладку
                            appState.currentTab.value = AppScreens.AddEvent.route

                        },
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
                // 1. Создание без id
                composable(
                    route = AppScreens.AddEvent.route
                ) {
                    AddEventRoute(
                        onBackPressed = { appState.navigate(AppScreens.Home.route) }
                    )
                }

                // Редактирование существующего события
                composable(
                    route = AppScreens.EditEvent.route,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    val args = entry.toRoute<EditEventArgs>() // <-- твоя функция toRoute() делает decodeArguments
                    AddEventRoute(
                        format = AddEventScreenFormat.RedactingEvent(args.id),
                        onBackPressed = { appState.navigate(AppScreens.Home.route) }
                    )
                }
            }

        }





    }
}*/


@Composable
private fun CustomSnackbar(
    data: SnackbarData,
//    type: SnackbarActions
) {

//    val background = when (type) {
//        SnackbarActions.EventCreated -> Color(0xFF4CAF50)
//        is SnackbarActions.EventFailed -> Color(0xFFF44336)
//        is SnackbarActions.Info -> Color(0xFF2196F3)
//    }

    Box(
        modifier = Modifier
            .padding(16.dp)
//            .background(background, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = data.visuals.message,
            color = Color.White
        )
    }
}

@Serializable
data class EditEventArgs(
    val id: String
)
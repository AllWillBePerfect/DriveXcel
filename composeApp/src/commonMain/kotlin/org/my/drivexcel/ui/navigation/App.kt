package org.my.drivexcel.ui.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.platform.WindowSize
import org.my.drivexcel.ui.theme.LocalWindowSize
import org.my.drivexcel.ui.theme.LocalWindowSizeClass
import org.my.drivexcel.ui.navigation.model.UserAuthorizedUiState
import org.my.drivexcel.ui.screens.event_editor.EventEditorRoute
import org.my.drivexcel.ui.screens.events.wide.EventsWithDetailsRoute
import org.my.drivexcel.ui.screens.login.LoginRoute
import org.my.drivexcel.ui.screens.settings.SettingsRoute
import org.my.drivexcel.ui.utils.nav.CustomNavigationSuiteScaffoldLayout
import org.my.drivexcel.ui.utils.nav.NavItem
import org.my.drivexcel.ui.utils.nav.Route
import org.my.drivexcel.ui.utils.nav.customNavigationSuiteType
import org.my.drivexcel.ui.utils.toMessage
import kotlin.reflect.KClass

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = koinViewModel()
) {

    val userAuthorizedUiState by viewModel.userAuthorizedState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    val windowSize = LocalWindowSize.current
    val windowSizeClass = LocalWindowSizeClass.current

    val wideNavigationRailState =
        rememberWideNavigationRailState(initialValue = WideNavigationRailValue.Collapsed)

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.messages.collectLatest { action ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = action.toMessage(),
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
        }
    }

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

    /*LaunchedEffect(userAuthorizedUiState) {
        when (userAuthorizedUiState) {
            UserAuthorizedUiState.Authorized -> {
                navController.navigate(Route.MainGraph) {
                    popUpTo(Route.LoginGraph) {
                        inclusive = true
                    }
                }
            }

            UserAuthorizedUiState.Unauthorized -> {
                navController.navigate(Route.LoginGraph) {
                    popUpTo(Route.MainGraph) {
                        inclusive = true
                    }
                }
            }

            else -> {}
        }
    }*/


    val navigationSuiteState =
        rememberNavigationSuiteScaffoldState(initialValue = NavigationSuiteScaffoldValue.Hidden)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentNavigationItem by remember(navBackStackEntry) {
        derivedStateOf {
            NavItem.entries.find { navigationItem ->
                navBackStackEntry.isRouteInHierarchy(
                    navigationItem.route::class
                )
            }
        }
    }

    LaunchedEffect(currentNavigationItem) {
        if (currentNavigationItem != null) {
            navigationSuiteState.show()
        } else {
            navigationSuiteState.hide()
        }
    }

    val customLayoutType = customNavigationSuiteType(windowSizeClass)



    CustomNavigationSuiteScaffoldLayout(
        navigationSuiteState = navigationSuiteState,
        layoutType = customLayoutType,
        currentNavigationItem = currentNavigationItem,
        onNavigationItemClick = { navItem ->
            navController.navigate(navItem.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        content = {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState
                    )
                }
            ) { innerPadding ->
                AppNavHost(
                    navController = navController,
                    userAuthorizedUiState = userAuthorizedUiState,
                )
            }

        }
    )


}

fun NavBackStackEntry?.isRouteInHierarchy(route: KClass<*>) =
    this?.destination?.hierarchy?.any {
        it.hasRoute(route)
    } == true


@Composable
fun AppNavHost(
    navController: NavHostController,
    userAuthorizedUiState: UserAuthorizedUiState
) {

    val startDestination = when (userAuthorizedUiState) {
        UserAuthorizedUiState.Loading -> null
        UserAuthorizedUiState.Authorized -> Route.MainGraph
        UserAuthorizedUiState.Unauthorized -> Route.LoginGraph
    }

    if (startDestination != null)
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {

            navigation<Route.LoginGraph>(
                startDestination = Route.Login
            ) {
                composable<Route.Login> {
                    LoginRoute(
                        onClickButtonNavigate = {
                            navController.navigate(Route.MainGraph) {
                                popUpTo(Route.LoginGraph) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
            }

            navigation<Route.MainGraph>(
                startDestination = Route.EventsGraph
            ) {
                navigation<Route.EventsGraph>(
                    startDestination = Route.EventsWithDetails,
                ) {

                    composable<Route.EventsWithDetails> {
                        EventsWithDetailsRoute(
                            navController = navController
                        )
                    }

                    composable<Route.CreateEvent> {
                        EventEditorRoute(
                            onBackPressedNavigate = { navController.popBackStack() }
                        )
                    }
                    composable<Route.UpdateEvent> {
                        EventEditorRoute(
                            onBackPressedNavigate = { navController.popBackStack() }
                        )
                    }
                }

                navigation<Route.SettingsGraph>(
                    startDestination = Route.Settings
                ) {
                    composable<Route.Settings> {
                        SettingsRoute(
                            onUnauthorizeButtonClicked = {
                                navController.navigate(Route.LoginGraph) {
                                    popUpTo(Route.MainGraph) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                }
            }

        }
}

/*navController.navigate(Route.MainGraph) {
    popUpTo(Route.AuthGraph) {
        inclusive = true
    }
}*/

/*navController.navigate(Route.AuthGraph) {
    popUpTo(Route.MainGraph) {
        inclusive = true
    }
}*/

/*composable<Route.Events> {
               EventsRoute(
                   onEventPressedNavigate = { eventId ->
                       navController.navigate(Route.Event(eventId)) {
                           launchSingleTop = true
                       }
                   },
                   onCreateEventNavigate = {
                       navController.navigate(Route.CreateEvent) {
                           launchSingleTop = true
                       }
                   },
                   onUpdateEventNavigate = { eventId ->
                       navController.navigate(Route.UpdateEvent(eventId)) {
                           launchSingleTop = true
                       }
                   }
               )
           }

           composable<Route.Event> {
               EventRoute(
                   onBackClickedNavigate = { navController.popBackStack() }
               )
           }*/
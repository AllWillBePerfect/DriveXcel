package org.my.drivexcel.ui.screens.home


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_menu
import drivexcel.composeapp.generated.resources.ic_settings
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.ui.navigation.ScreenRoutes
import org.my.drivexcel.ui.screens.events.EventsRoute


@Composable
fun HomeRoute(
    viewModel: HomeViewModel = koinViewModel(),
    onCreateEvent: () -> Unit,
    onUpdateEvent: (id: String) -> Unit
) {
    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }


    /*LaunchedEffect(Unit) {
        viewModel.messages.collect { action ->
            snackbarHostState.showSnackbar(action.toMessage(), "hello")
        }
    }
*/


    HomeScreen(
        snackbarHostState = snackbarHostState,
        navController = navController,
        onCreateEvent = {
            navController.navigate(ScreenRoutes.EventCreate.route)
        },
        onUpdateEvent = {
            navController.navigate(ScreenRoutes.EventUpdate.createRoute(it))
        },
        onBackPressed = { navController.popBackStack() }
    )
}

@Composable
private fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    onCreateEvent: () -> Unit,
    onUpdateEvent: (id: String) -> Unit,
    onBackPressed: () -> Unit
) {


    HomeWrapper(
        navController = navController,
        snackbarHostState = snackbarHostState,
        content = { innerPadding ->
            HomeContent(
                innerPadding = innerPadding,
                navController = navController,
                onCreateEvent = onCreateEvent,
                onUpdateEvent = onUpdateEvent,
                onBackPressed = onBackPressed
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeWrapper(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    content: @Composable (PaddingValues) -> Unit,
) {

    val currentDestination =
        navController.currentBackStackEntryFlow.collectAsStateWithLifecycle(null)

    val currentTab by remember {
        derivedStateOf {
            currentDestination.value?.destination.homeTab()
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {

            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {

                HomeRoutes.entries.forEach { destination ->

                    /*val selected = currentDestination.value?.destination?.route
                                    ?.startsWith(destination.route) == true
*/


                    NavigationBarItem(
                        selected = currentTab == destination,
                        onClick = {
                            /*navController.navigate(destination.route) {

                                *//*popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }*//*

                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }*/

                            /*val currentTab = navController.currentDestination?.homeTab()

                            if (currentTab == destination) return@NavigationBarItem

                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }*/

                            val currentTab = navController.currentDestination?.homeTab()

                            if (currentTab == destination) return@NavigationBarItem

                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painterResource(destination.icon),
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun HomeContent(
    innerPadding: PaddingValues,
    navController: NavHostController,
    onCreateEvent: () -> Unit,
    onUpdateEvent: (id: String) -> Unit,
    onBackPressed: () -> Unit
) {

    NavHost(
        navController = navController,
        startDestination = HomeRoutes.EVENTS.route,
        modifier = Modifier
            .padding(bottom = innerPadding.calculateBottomPadding())
            .fillMaxSize(),
        enterTransition = { tabEnter() },
        exitTransition = { tabExit() },
        popEnterTransition = { tabEnter() },
        popExitTransition = { tabExit() }
    ) {

        navigation(
            startDestination = "events_list",
            route = HomeRoutes.EVENTS.route,
//            enterTransition = { EnterTransition.None },
//            exitTransition = {
//                scaleOut(
//                    targetScale = 1.1f,
//                    animationSpec = tween(300)
//                ) + fadeOut(animationSpec = tween(300))
//            },
//            popEnterTransition = {
//                scaleIn(
//                    initialScale = 0.9f,
//                    animationSpec = tween(300)
//                ) + fadeIn(animationSpec = tween(300))
//            },
//            popExitTransition = { ExitTransition.None }
        ) {

            composable(
                route = "events_list",
            ) {
                EventsRoute(
                    onEventPressedNavigate = { id ->
                        navController.navigate("event/$id")
                    },
                    onCreateEventNavigate = onCreateEvent,
                    onUpdateEventNavigate = onUpdateEvent
                )
            }

            composable(
                route = "event/{eventId}",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "drivexcel://event/{eventId}"
                    }
                ),
                enterTransition = {
                    scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    scaleOut(
                        targetScale = 1.1f,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    scaleIn(
                        initialScale = 1.1f,
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    scaleOut(
                        targetScale = 0.9f,
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) { backStackEntry ->

                /*val id =
                    backStackEntry.arguments?.getString("eventId")!!

                EventScreen(
                    id = id,
                    onUpdateEvent = onUpdateEvent
                )*/

                /*EventRoute(
                    onBackClickedNavigate = {
                        navController.popBackStack()
                    }
                )*/
            }
        }

        /*navigation(
            startDestination = "setting_graph",
            route = HomeRoutes.SETTINGS.route,
        ) {
            composable(
                route = "setting_graph"
            ) {
                SettingsRoute()
            }
        }*/

        /*composable(
            route = ScreenRoutes.EventCreate.route,
        ) {
            EventEditorRoute(
                onBackPressed = onBackPressed
            )
        }

        composable(
            route = ScreenRoutes.EventUpdate.route,
            arguments = listOf(
                navArgument("eventId") {
                    type = NavType.StringType
                }
            ),
        ) {
            EventEditorRoute(
                onBackPressed = onBackPressed
            )
        }*/
    }
}

fun NavDestination?.homeTab(): HomeRoutes? {
    return this?.hierarchy
        ?.mapNotNull { dest ->
            HomeRoutes.entries.find { it.route == dest.route }
        }
        ?.firstOrNull()
}

fun HomeRoutes.index(): Int {
    return HomeRoutes.entries.indexOf(this)
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.tabEnter(): EnterTransition {

    val fromTab = initialState.destination.homeTab()
    val toTab = targetState.destination.homeTab()

    if (fromTab == null || toTab == null) return EnterTransition.None

    return if (toTab.index() > fromTab.index()) {
        slideInHorizontally { it }
    } else {
        slideInHorizontally { -it }
    }
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.tabExit(): ExitTransition {

    val fromTab = initialState.destination.homeTab()
    val toTab = targetState.destination.homeTab()

    if (fromTab == null || toTab == null) return ExitTransition.None

    return if (toTab.index() > fromTab.index()) {
        slideOutHorizontally { -it }
    } else {
        slideOutHorizontally { it }
    }
}

/*
@Composable
private fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    onCreateEvent: () -> Unit,
    onUpdateEvent: (id: String) -> Unit
) {

    val navController = rememberNavController()
    var currentTab by rememberSaveable {
        mutableStateOf(HomeRoutes.EVENTS)
    }

    HomeWrapper(
        navController = navController,
        snackbarHostState = snackbarHostState,
        switchTab = {
            currentTab = it
        },
        content = { innerPadding ->
            HomeContent(
                innerPadding = innerPadding,
                navController = navController,
                currentTab = currentTab,
                onCreateEvent = onCreateEvent,
                onUpdateEvent = onUpdateEvent
            )
        }
    )
}
*/

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeWrapper(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    switchTab: (HomeRoutes) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {

    val scope = rememberCoroutineScope()

    val startDestination = HomeRoutes.EVENTS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Main"
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)

        },
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                HomeRoutes.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
//                            navController.navigate(route = destination.route)
                            selectedDestination = index
//                            currentTab = MainRoutes.values()[index]
                            switchTab(HomeRoutes.values()[index])
                        },
                        icon = {
                            Icon(
                                painterResource(destination.icon),
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
*/

/*
@Composable
private fun HomeContent(
    innerPadding: PaddingValues,
    navController: NavHostController,
    currentTab: HomeRoutes,
    onCreateEvent: () -> Unit,
    onUpdateEvent: (id: String) -> Unit
) {

    AnimatedContent(
        targetState = currentTab,
        transitionSpec = {

            if (targetState == HomeRoutes.SETTINGS) {
                slideInHorizontally { it } togetherWith
                        slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } togetherWith
                        slideOutHorizontally { it }
            }
        },
        modifier = Modifier.padding(innerPadding).fillMaxSize()
    ) { tab ->

        when (tab) {

            HomeRoutes.EVENTS -> {
                Column() {

                    Button(onClick = onCreateEvent) {
                        Text("Создать")
                    }

                    */
/*Button(onClick = { onUpdateEvent() }) {
                        Text("Редактировать")
                    }*//*


                    EventsRoute(
                        onUpdateEvent = onUpdateEvent
                    )
                }
            }

            HomeRoutes.SETTINGS -> {
                SettingsRoute()
            }
        }
    }

    */
/*
        NavHost(
            navController = navController,
            startDestination = MainRoutes.EVENTS.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainRoutes.EVENTS.route) {
                Column(
                ) {
                    Button(
                        onClick = onCreateEvent
                    ) {
                        Text(
                            text = "Создать"
                        )
                    }
                    Button(
                        onClick = onUpdateEvent
                    ) {
                        Text(
                            text = "Редактировать"
                        )
                    }
                    EventsRoute()


                }
            }

            composable(MainRoutes.SETTINGS.route) {
                SettingsRoute()
            }
        }
    *//*



}
*/

enum class HomeRoutes(
    val route: String,
    val icon: DrawableResource,
    val contentDescription: String,
    val label: String
) {
    EVENTS(
        route = "events",
        icon = Res.drawable.ic_menu,
        contentDescription = "",
        label = "Список"
    ),
    SETTINGS(
        route = "settings",
        icon = Res.drawable.ic_settings,
        contentDescription = "",
        label = "Настройки"
    ),

}
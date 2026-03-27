package org.my.drivexcel.v4.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject
import org.my.drivexcel.v4.ui.screens.event_editor.EventEditorRoute
import org.my.drivexcel.v4.ui.screens.home.HomeRoute
import org.my.drivexcel.v4.ui.utils.SnackbarManager
import org.my.drivexcel.v4.ui.utils.toMessage

@Composable
fun App(
    navHostController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = koinInject()
) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarManager.messages.collectLatest { action ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = action.toMessage(),
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
        }
    }

    val enterFromRight = {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(300)
        )
    }

    val exitToLeft = {
        slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(300)
        )
    }

    val enterFromLeft = {
        slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(300)
        )
    }

    val exitToRight = {
        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(300)
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navHostController,
            startDestination = ScreenRoutes.Main.route
        ) {

            composable(
                route = ScreenRoutes.Main.route,
                enterTransition = { enterFromLeft() },
                exitTransition = { exitToLeft() },
                popEnterTransition = { enterFromLeft() },
                popExitTransition = { exitToRight() }
            ) {
                HomeRoute(
                    onCreateEvent = {
                        navHostController.navigate(ScreenRoutes.EventCreate.route)
                    },
                    onUpdateEvent = {
                        navHostController.navigate(
                            ScreenRoutes.EventUpdate.createRoute(it)
                        )
                    }
                )
            }

            composable(
                route = ScreenRoutes.EventCreate.route,
                enterTransition = { enterFromRight() },
                exitTransition = { exitToLeft() },
                popEnterTransition = { enterFromLeft() },
                popExitTransition = { exitToRight() }
            ) {
                EventEditorRoute(
                    onBackPressed = { navHostController.popBackStack() }
                )
            }

            composable(
                route = ScreenRoutes.EventUpdate.route,
                arguments = listOf(
                    navArgument("eventId") {
                        type = NavType.StringType
                    }
                ),
                enterTransition = { enterFromRight() },
                exitTransition = { exitToLeft() },
                popEnterTransition = { enterFromLeft() },
                popExitTransition = { exitToRight() }
            ) {
                EventEditorRoute(
                    onBackPressed = { navHostController.popBackStack() }
                )
            }
        }

    }

}
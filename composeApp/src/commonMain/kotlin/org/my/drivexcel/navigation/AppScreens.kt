package org.my.drivexcel.navigation

import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.DrawableResource

sealed class AppScreens(val route: String) {
    data object Login : AppScreens(route = "login")
    data object Home : AppScreens(route = "home")
    data object Details : AppScreens(route = "details")
    data object HomeWithDetails : AppScreens(route = "home_with_details")
    data object Settings : AppScreens(route = "settings")

}

enum class RailScreens(
    val route: String,
    val image: DrawableResource,
    val navigateTo: String
) {


    Home(
        route = "Home",
        image = Res.drawable.compose_multiplatform,
        navigateTo = AppScreens.HomeWithDetails.route

    ),
    Settings(
        route = "Settings",
        image = Res.drawable.compose_multiplatform,
        navigateTo = AppScreens.Settings.route


    );

    fun dfdfd() {
        Res.drawable.compose_multiplatform
    }

}
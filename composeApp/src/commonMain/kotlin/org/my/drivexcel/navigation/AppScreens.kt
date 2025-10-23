package org.my.drivexcel.navigation

import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.compose_multiplatform
import drivexcel.composeapp.generated.resources.ic_events_list
import drivexcel.composeapp.generated.resources.ic_settings
import org.jetbrains.compose.resources.DrawableResource

sealed class AppScreens(val route: String) {
    data object Home : AppScreens(route = "home")
    data object Settings : AppScreens(route = "settings")
    data object AddEvent : AppScreens(route = "add_event")

}

enum class RailScreens(
    val route: String,
    val image: DrawableResource,
    val navigateTo: String
) {


    Home(
        route = "Директории",
        image = Res.drawable.ic_events_list,
        navigateTo = AppScreens.Home.route

    ),
    Settings(
        route = "Настройки",
        image = Res.drawable.ic_settings,
        navigateTo = AppScreens.Settings.route
    );

    fun dfdfd() {
        Res.drawable.compose_multiplatform
    }

}
package org.my.drivexcel.v4.ui.navigation

sealed class ScreenRoutes(val route: String) {
    object Main : ScreenRoutes(route = "main")
    object EventCreate : ScreenRoutes(route = "create_event")
    object EventUpdate : ScreenRoutes(route = "update_event/{eventId}") {
        fun createRoute(eventId: String) = "update_event/$eventId"
    }
}

sealed class EventRoutes(val route: String, private val routePrefix: String) {
    object Users : EventRoutes(route = "users/{$EVENT_ID_NAV_ARGUMENT}", routePrefix = "users")
    object Import : EventRoutes(route = "import/{$EVENT_ID_NAV_ARGUMENT}", routePrefix = "import")

    fun createRoute(eventId: String): String {
        return "$routePrefix/$eventId"
    }

    companion object {
        const val EVENT_ID_NAV_ARGUMENT = "eventId"
    }

}

package org.my.drivexcel.ui.utils.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationItemIconPosition
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Events : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object CreateEvent : Route

    @Serializable
    data class UpdateEvent(val eventId: String) : Route

    @Serializable
    data class Event(val eventId: String) : Route

    @Serializable
    data object EventsWithDetails : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object EventsGraph : Route

    @Serializable
    data object SettingsGraph : Route

    @Serializable
    data object LoginGraph : Route

    @Serializable
    data object MainGraph : Route


}


enum class NavItem(
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
) {
    HOME(
        route = Route.EventsGraph,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        label = "Список"
    ),
    SETTINGS(
        route = Route.SettingsGraph,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        label = "Настройки"
    )
}

fun NavDestination?.topLevelRoute(): Route? {
    return this?.hierarchy?.firstNotNullOfOrNull { dest ->
        when (dest.route) {
            Route.EventsGraph::class.qualifiedName -> Route.EventsGraph
            Route.SettingsGraph::class.qualifiedName -> Route.SettingsGraph
            else -> null
        }
    }
}

@Composable
fun CustomNavigationSuiteScaffoldLayout(
    navigationSuiteState: NavigationSuiteScaffoldState,
    layoutType: NavigationSuiteType,
    currentNavigationItem: NavItem?,
    onNavigationItemClick: (NavItem) -> Unit,
    colors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
    content: @Composable BoxScope.() -> Unit,
) {

    val scope = rememberCoroutineScope()
    val wideNavigationRailState = rememberWideNavigationRailState()
    LaunchedEffect(layoutType) {
        when (layoutType) {
            NavigationSuiteType.WideNavigationRailExpanded ->
                wideNavigationRailState.expand()

            NavigationSuiteType.WideNavigationRailCollapsed ->
                wideNavigationRailState.collapse()

            else -> Unit
        }
    }

    NavigationSuiteScaffoldLayout(
        state = navigationSuiteState,
        navigationSuite = {
            when (layoutType) {
                NavigationSuiteType.ShortNavigationBarCompact,
                NavigationSuiteType.ShortNavigationBarMedium,
                NavigationSuiteType.NavigationBar -> {
                    ShortNavigationBar(
                        modifier = Modifier,
                        containerColor = colors.shortNavigationBarContainerColor,
                        contentColor = colors.shortNavigationBarContentColor,
                        content = {
                            NavItem.entries.forEach { item ->
                                ShortNavigationBarItem(
                                    iconPosition = if (layoutType == NavigationSuiteType.ShortNavigationBarCompact) NavigationItemIconPosition.Top else NavigationItemIconPosition.Start,
                                    icon = {
                                        Icon(
                                            imageVector = if (currentNavigationItem == item) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = null,
                                        )
                                    },
                                    label = { Text(item.label) },
                                    selected = currentNavigationItem == item,
                                    onClick = { onNavigationItemClick(item) },
                                )
                            }
                        },
                    )
                }

                NavigationSuiteType.WideNavigationRailCollapsed,
                NavigationSuiteType.WideNavigationRailExpanded -> {
                    WideNavigationRail(
                        state = wideNavigationRailState,
                        modifier = Modifier,
                        header = {
                            IconButton(
                                modifier = Modifier.padding(start = 24.dp),
                                onClick = {
                                    scope.launch {
                                        if (wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded)
                                            wideNavigationRailState.collapse()
                                        else wideNavigationRailState.expand()
                                    }
                                },
                            ) {
                                if (wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded) {
                                    Icon(Icons.AutoMirrored.Filled.MenuOpen, "Collapse rail")
                                } else {
                                    Icon(Icons.Filled.Menu, "Expand rail")
                                }
                            }
                        },
                        colors = colors.wideNavigationRailColors,
                        content = {
                            NavItem.entries.forEach { item ->
                                WideNavigationRailItem(
                                    modifier = Modifier.padding(
                                        if (wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded) 5.dp else 0.dp
                                    ),
                                    iconPosition = if (wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded) {
                                        NavigationItemIconPosition.Start
                                    } else {
                                        NavigationItemIconPosition.Top
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentNavigationItem == item) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = null,
                                        )
                                    },
                                    label = { Text(item.label) },
                                    selected = currentNavigationItem == item,
                                    onClick = { onNavigationItemClick(item) },
                                    railExpanded = wideNavigationRailState.targetValue == WideNavigationRailValue.Expanded,
                                )
                            }
                        },
                    )
                }

                NavigationSuiteType.NavigationRail -> {
                    NavigationRail(
                        modifier = Modifier,
                        header = { },
                        containerColor = colors.navigationRailContainerColor,
                        contentColor = colors.navigationRailContentColor,
                    ) {
                        Spacer(Modifier.weight(1f))
                        NavItem.entries.forEach { item ->
                            NavigationRailItem(
                                icon = {
                                    Icon(
                                        imageVector = if (currentNavigationItem == item) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = null,
                                    )
                                },
                                label = { Text(item.label) },
                                selected = currentNavigationItem == item,
                                onClick = { onNavigationItemClick(item) },
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                }

                NavigationSuiteType.NavigationDrawer -> {
                    PermanentDrawerSheet(
                        modifier = Modifier,
                        drawerContainerColor = colors.navigationDrawerContainerColor,
                        drawerContentColor = colors.navigationDrawerContentColor,
                    ) {
                        NavItem.entries.forEach { item ->
                            NavigationDrawerItem(
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                                icon = {
                                    Icon(
                                        imageVector = if (currentNavigationItem == item) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = null,
                                    )
                                },
                                label = { Text(item.label) },
                                selected = currentNavigationItem == item,
                                onClick = { onNavigationItemClick(item) },
                            )
                        }
                    }
                }
            }
        },
        layoutType = layoutType,
        content = {
            MainContentBox(
                navigationSuiteState = navigationSuiteState,
                layoutType = layoutType,
                content = content
            )
        }
    )
}

@Composable
fun MainContentBox(
    navigationSuiteState: NavigationSuiteScaffoldState,
    layoutType: NavigationSuiteType,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(
                if (
                    navigationSuiteState.currentValue ==
                    NavigationSuiteScaffoldValue.Hidden &&
                    !navigationSuiteState.isAnimating
                ) {
                    WindowInsets() // NoWindowInsets
                } else {
                    when (layoutType) {
                        NavigationSuiteType.ShortNavigationBarCompact,
                        NavigationSuiteType.ShortNavigationBarMedium,
                        NavigationSuiteType.NavigationBar ->
                            NavigationBarDefaults.windowInsets.only(
                                WindowInsetsSides.Bottom
                            )

                        NavigationSuiteType.NavigationRail ->
                            NavigationRailDefaults.windowInsets.only(
                                WindowInsetsSides.Start
                            )

                        NavigationSuiteType.NavigationDrawer ->
                            DrawerDefaults.windowInsets.only(
                                WindowInsetsSides.Start
                            )

                        else -> WindowInsets()
                    }
                }
            ),
        content = content
    )
}
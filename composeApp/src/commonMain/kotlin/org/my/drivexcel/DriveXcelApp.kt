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
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.cio.HttpServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
import org.my.drivexcel.ui.screens.connection.ConnectionRoute
import org.my.drivexcel.ui.screens.home.HomeRoute
import org.my.drivexcel.ui.screens.settings.SettingsRoute
import org.my.drivexcel.utils.ActionsManager
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo
import javax.jmdns.ServiceListener
import kotlin.time.Duration

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
                windowSize = windowSize,
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
fun DriveXcelNavigation(
    appState: DriveXcelAppState,
    windowSize: WindowSize,
    openDrawer: () -> Unit
) {

    val addEventViewModel: AddEventViewModel = koinViewModel()
    val webSocketsViewModel: WebSocketsViewModel = koinViewModel()

    val tabs = listOf(
        AppScreens.Home,
        AppScreens.Settings,
        AppScreens.AddEvent,
        AppScreens.EditEvent,
        AppScreens.Connection
    )

    // NavController для каждой вкладки
    val navControllers = remember {
        mapOf(
            AppScreens.Home to appState.homeNavController,
            AppScreens.Settings to appState.settingsNavController,
            AppScreens.AddEvent to appState.addEventNavController,
            AppScreens.EditEvent to appState.redactingNavController,
            AppScreens.Connection to appState.connectionNavController,
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

                    AppScreens.Connection -> ConnectionNavHost(
                        webSocketsViewModel = webSocketsViewModel,
                        navController = navController,
                        windowSize = windowSize,
                        onBackPressed = { appState.navigate(AppScreens.Home.route) }
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
            val viewModel: AddEventViewModel = koinViewModel()
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

@Composable
fun ConnectionNavHost(
    webSocketsViewModel: WebSocketsViewModel,
    navController: NavHostController,
    windowSize: WindowSize,
    onBackPressed: () -> Unit,
) {
    NavHost(navController, startDestination = AppScreens.Connection.route) {

        composable(AppScreens.Connection.route) {
            ConnectionRoute(
                webSocketsViewModel = webSocketsViewModel,
                windowSize = windowSize,
                onBackPressed = onBackPressed,
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


class MdnsService private constructor(
    private val serviceType: String,
    private val name: String,
    private val port: Int
) {

    private var jmdns: JmDNS? = null

    private fun getBindAddress(): InetAddress {
        // Android: wlan0
        getWifiInetAddress()?.let { return it }

        println("Wi-fi not found :(")

        // Desktop: нормальный local host
        return InetAddress.getLocalHost()
    }

    fun start(callback: (host: String, port: Int, name: String) -> Unit) {
        val addr = getBindAddress()
        jmdns = JmDNS.create(addr)

        // Регистрируем сервис
        val serviceInfo = ServiceInfo.create(serviceType, name, port, "KMP Service")
        jmdns?.registerService(serviceInfo)

        // Слушаем сервисы других устройств
        jmdns?.addServiceListener(serviceType, object : ServiceListener {
            override fun serviceAdded(event: ServiceEvent?) {
                // Запрашиваем полное info
                jmdns?.requestServiceInfo(serviceType, event?.name)
            }

            override fun serviceRemoved(event: ServiceEvent?) {}

            override fun serviceResolved(event: ServiceEvent?) {
                val info = event?.info ?: return
                val host = info.hostAddresses.firstOrNull() ?: return
                callback(host, info.port, info.name)
            }
        })
    }

    // Остановка сервиса
    fun stop() {
        jmdns?.unregisterAllServices()
        jmdns?.close()
    }

    private fun getWifiInetAddress(): InetAddress? {
        val interfaces = NetworkInterface.getNetworkInterfaces() ?: return null

        for (networkInterface in interfaces) {
            if (!networkInterface.isUp || networkInterface.isLoopback) continue

            // Wi-Fi интерфейсы на Android обычно wlan0
            if (networkInterface.name.startsWith("wlan")) {
                val addresses = networkInterface.inetAddresses
                for (address in addresses) {
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address
                    }
                }
            }
        }
        return null
    }

    // Builder для удобного создания
    class Builder {
        private var serviceType: String = "_drivexcel._tcp.local."
        private var name: String = "MyDevice"
        private var port: Int = 5000

        fun serviceType(type: String) = apply { this.serviceType = type }
        fun name(name: String) = apply { this.name = name }
        fun port(port: Int) = apply { this.port = port }

        fun build(): MdnsService {
            return MdnsService(serviceType, name, port)
        }
    }

    @Serializable
    @SerialName("mdns_device")
    data class Device(val host: String, val port: Int, val name: String) {
        companion object {
            fun default() = Device(
                host = "192.168.0.100",
                port = 5000,
                name = "Sample device"
            )
        }
    }

}

fun startWebSocketServer(
    port: Int,
    mdnsManager: MdnsManager,
    onConnectionRequest: suspend (
        from: MdnsService.Device,
        respond: suspend (Boolean) -> Unit
    ) -> Unit
) {


    embeddedServer(CIO, port) {
        install(WebSockets)

        routing {
            webSocket("/chat") {

                for (frame in incoming) {
                    val text = (frame as? Frame.Text)?.readText() ?: continue
                    val msg = Json.decodeFromString<WsMessage>(text)

                    when (msg.type) {

                        // ------------ REQUEST CONNECT ------------
                        "request_connect" -> {

                            // Ищем устройство среди mDNS-найденных

                            println("finding devices: ${mdnsManager.getDevices()}")

                            val device = mdnsManager
                                .getDevices()
                                .find {
                                    println("device: $it ::: message: $msg")
                                    it.name == msg.from.name
                                }

                            if (device == null) {
                                println("⚠ Unknown device tried to connect: ${msg.from}")
                                continue
                            }

                            // колбэк, который ViewModel вызовет после approve/reject
                            val respond: suspend (Boolean) -> Unit = { approved ->
                                val response = WsMessage(
                                    type = if (approved) "approved" else "rejected",
                                    from = device
                                )
                                outgoing.send(Frame.Text(Json.encodeToString(response)))
                            }

                            // передаём в ViewModel — теперь with Device!
                            onConnectionRequest(device, respond)
                        }
                    }
                }
            }

            /*webSocket("/chat") {
                var deviceForSession: MdnsService.Device? = null
                val session = ChatSession(this)

                try {
                    for (frame in incoming) {
                        val text = (frame as? Frame.Text)?.readText() ?: continue
                        val msg = Json.decodeFromString<WsMessage>(text)

                        when (msg.type) {
                            "request_connect" -> {
                                val device = mdnsManager.getDevices().find { it.name == msg.from.name }
                                if (device == null) continue

                                deviceForSession = device

                                // создаём callback для approve/reject
                                val respond: suspend (Boolean) -> Unit = { approved ->
                                    val response = WsMessage(
                                        type = if (approved) "approved" else "rejected",
                                        from = device
                                    )
                                    outgoing.send(Frame.Text(Json.encodeToString(response)))
                                }

                                // передаём в ViewModel
                                onConnectionRequest(device, respond)
                            }

                            "chat_message" -> {
                                // можно сюда добавить обработку приходящих сообщений
                                println("Message from ${deviceForSession?.name}: ${msg.content}")
                            }
                        }
                    }
                } finally {
                    // удаляем сессию при разрыве
                    deviceForSession?.let { device ->
                        ConnectionViewModel.chatSessions.remove(device)
                    }
                }
            }*/

        }
    }.start(wait = false)
}




interface KtorHttpPlatformProvider {

    val client: HttpClient

    fun createHttpClient(): HttpClient
}

class KtorClientServer(
    val ktorHttpPlatformProvider: KtorHttpPlatformProvider
) {
    suspend fun sendMessageToDevice(device: MdnsService.Device, message: String) {
        val client = ktorHttpPlatformProvider.client

        client.webSocket(
            host = device.host,
            port = device.port,
            path = "/chat"
        ) {
            send(message)

            for (frame in incoming) {
                if (frame is Frame.Text) {
                    println("Received: ${frame.readText()}")
                }
            }
        }
    }
}



interface MdnsManagerProvider {
    fun provide(): MdnsManager
}

class MdnsManager(
    private val serviceName: String = "JvmDesktop",
    private val serviceType: String = "_drivexcel._tcp.local.",
    private val port: Int = 5000
) {

    private var mdns: MdnsService? = null
    private val devices = mutableSetOf<MdnsService.Device>()

    /**
     * Запускает публикацию сервиса и поиск других устройств
     */
    fun start(callback: (MdnsService.Device) -> Unit = {}) {
        if (mdns != null) return // уже запущен

        mdns = MdnsService.Builder()
            .serviceType(serviceType)
            .name(serviceName)
            .port(port)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            mdns?.start { host, port, name ->
                val device = MdnsService.Device(host, port, name)

                if (devices.add(device)) {
                        println("Found mDNS device: $device")
                    // callback вызываем на главном потоке, если нужно UI
                    callback(device)
                }
            }
        }
    }

    /**
     * Получить список всех найденных устройств
     */
    fun getDevices(): List<MdnsService.Device> = devices.toList()

    /**
     * Останавливает публикацию и поиск
     */
    fun stop() {
        CoroutineScope(Dispatchers.IO).launch {
            mdns?.stop()
            mdns = null
            devices.clear()
            println("mDNS service stopped")
        }
    }
}

@Serializable
data class WsMessage(
    val type: String,       // "request_connect", "approved", "rejected", "data"
    val from: MdnsService.Device,       // имя устройства
    val payload: String? = null
)


@Serializable
data class DevicePair(
    val to: MdnsService.Device,
    val from: MdnsService.Device,
)

suspend fun HttpClient.requestConnection(
    fromDevice: MdnsService.Device,
    toDevice: MdnsService.Device
): Boolean {

    val result = CompletableDeferred<Boolean>()

    webSocket(host = toDevice.host, port = toDevice.port, path = "/chat") {
        // 1. отправляем запрос
        val json = Json.encodeToString(
            WsMessage(
                type = "request_connect",
                from = fromDevice
            )
        )
        send(json)

        // 2. слушаем ответ
        for (frame in incoming) {
            val text = (frame as? Frame.Text)?.readText() ?: continue
            val msg = Json.decodeFromString<WsMessage>(text)

            when (msg.type) {
                "approved" -> {
                    result.complete(true)
                    close() // закрываем сокет handshake
                }

                "rejected" -> {
                    result.complete(false)
                    close()
                }
            }
        }
    }

    return result.await()
}

interface LocalIpAddress {
    fun getIp(): String?
}



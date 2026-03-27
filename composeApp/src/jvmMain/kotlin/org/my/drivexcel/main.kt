package org.my.drivexcel

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.di.initKoinWithModules
import org.my.drivexcel.platform.utils.WindowSizeClass
import org.my.drivexcel.theme.DriveXcelAppTheme
import org.my.drivexcel.v4.ui.navigation.App
import java.awt.Dimension
import java.net.Inet4Address
import java.net.NetworkInterface


fun main() {


    initKoinWithModules()
    application {

        /*val ktorClient: KtorClientServer = GlobalContext.get().get()

        // 1. Запускаем WebSocket сервер
        startWebSocketServer(port = 5000) { deviceName, answer ->

        }

        // 2. Провайдер и клиент
        val provider = ktorClient.ktorHttpPlatformProvider
        val client = WebSocketClient(provider)

        mdnsManager = MdnsManager(
            serviceName = "JvmDesktop"
        )

        mdnsManager.start { device ->
            println("Connecting to device: $device")

            CoroutineScope(Dispatchers.IO).launch {
                client.sendMessage(
                    host = device.host,
                    port = device.port,
                    message = "Ping from Android!"
                ) { resp ->
                    println("Client received: $resp")
                }
            }
        }*/


        /*// 3. Запускаем mDNS сервис + поиск других устройств
        registerMdnsService { device ->
            println("Connecting to device via WS: $device")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    client.sendMessage(
                        host = device.host,
                        port = device.port,
                        message = "Ping from Desktop!"
                    ) { response ->
                        println("Client received: $response")
                    }
                } catch (e: Exception) {
                    println("Failed to connect: ${e.message}")
                }
            }
        }*/

        Window(
            onCloseRequest = {
                exitApplication()
            },
            title = "DriveXcel",
            state = rememberWindowState(
                size = DpSize(
                    width = 400.dp,
                    height = 800.dp
                )
            ),
        ) {
            window.minimumSize = Dimension(400, 400)

            val themeViewModel: ThemeViewModel = koinViewModel()
            val systemDark = isSystemInDarkTheme()
            val uiState by themeViewModel.userSettings.collectAsState()

            val themeSettings = ThemeSettings(
                isDarkMode = uiState.shouldUseDarkTheme(systemDark)
            )


            val windowSizeClass: WindowSizeClass = koinInject()

            DriveXcelAppTheme(
                darkTheme = themeSettings.isDarkMode,
                windowSizeClass = windowSizeClass
            ) {
//                DriveXcelApp(
//                )
                App()
            }
        }
    }


}

private fun registerMdnsService(callback: (MdnsService.Device) -> Unit = {}) {
    CoroutineScope(Dispatchers.IO).launch {
        val mdns = MdnsService.Builder()
            .serviceType("_drivexcel._tcp.local.")
            .name("JvmDesktop")
            .port(5000)
            .build()

        val devices = mutableSetOf<MdnsService.Device>()


// Поиск сервисов
        mdns.start { host, port, name ->
            val device = MdnsService.Device(host, port, name)
            if (devices.add(device)) {
                println("Found service: $device")
                callback.invoke(device)

            }
        }

// Остановка
//    mdns.stop()
    }
}


private data class ThemeSettings(
    val isDarkMode: Boolean
)


/*
fun isSystemInDarkThemeFlow() = callbackFlow {
    val currentDark = isSystemDark()
    trySend(currentDark)

    val listener = PropertyChangeListener {
        trySend(isSystemDark())
    }

    Toolkit.getDefaultToolkit().addPropertyChangeListener("win.lightTheme", listener)
    Toolkit.getDefaultToolkit().addPropertyChangeListener("mac.themeChanged", listener)

    awaitClose {
        Toolkit.getDefaultToolkit().removePropertyChangeListener(listener)
    }
}
    .distinctUntilChanged()
    .conflate()

private fun isSystemDark(): Boolean {
    val osName = System.getProperty("os.name").lowercase()
    return when {
        osName.contains("mac") -> {
            val theme = Runtime.getRuntime().exec(arrayOf("defaults", "read", "-g", "AppleInterfaceStyle"))
                .inputStream.bufferedReader().readText().trim()
            theme.equals("dark", ignoreCase = true)
        }
        osName.contains("win") -> {
            val process = Runtime.getRuntime().exec(
                arrayOf("reg", "query",
                    "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                    "/v", "AppsUseLightTheme")
            )
            val output = process.inputStream.bufferedReader().readText()
            "0x0" in output // 0x0 = тёмная тема
        }
        else -> false // на Linux определить сложно — лучше просто возвращать false или настраивать вручную
    }
}
*/

class KtorHttpPlatformProviderJvm : KtorHttpPlatformProvider {

    override val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(WebSockets)
        }
    }

    override fun createHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(WebSockets)
        }
    }
}

class MdnsManagerProviderJvm() : MdnsManagerProvider {
    override fun provide(): MdnsManager {
        val deviceName = try {
            java.net.InetAddress.getLocalHost().hostName
        } catch (e: Exception) {
            "JvmDesktop"
        }
        return MdnsManager(
            serviceName = deviceName
        )
    }
}

class LocalIpAddressJvm : LocalIpAddress {
    override fun getIp(): String? {
        return NetworkInterface.getNetworkInterfaces().toList()
            .flatMap { it.inetAddresses.toList() }
            .firstOrNull { it is Inet4Address && !it.isLoopbackAddress }
            ?.hostAddress
    }
}

class CoroutineDispatcherProviderJvm : CoroutineDispatcherProvider {
    override fun main(): CoroutineDispatcher {
        return Dispatchers.Main
    }
}

class MdnsServiceBuilderProviderJvm : V2.MdnsServiceBuilderProvider {
    override fun provide(): V2.MdnsServiceBuilder {
        val deviceName = try {
            java.net.InetAddress.getLocalHost().hostName
        } catch (e: Exception) {
            "JvmDesktop"
        }
        return V2.MdnsServiceBuilder().config {
            copy(
                name = deviceName
            )
        }
    }
}

class KtorClientProviderJvm : V2.KtorClientProvider {

    override val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(WebSockets)
        }
    }

}


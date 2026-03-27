package org.my.drivexcel

import android.app.Activity
import android.content.Context
import android.content.Context.NSD_SERVICE
import android.content.res.Configuration
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.koinInject
import org.my.drivexcel.platform.utils.WindowSizeClass
import org.my.drivexcel.theme.DriveXcelAppTheme
import org.my.drivexcel.v4.ui.navigation.App


class MainActivity : ComponentActivity() {

    val themeViewModel: ThemeViewModel by viewModel()

    private lateinit var mdnsManager: MdnsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*// 1. Запускаем WebSocket сервер
        startWebSocketServer(port = 5000)

        // 2. Провайдер и клиент
        val provider = themeViewModel.ktorClientServer.ktorHttpPlatformProvider
        val client = WebSocketClient(provider)

        mdnsManager = MdnsManager(
            serviceName = "Android"
        )

        mdnsManager.start { device ->
            println("Connecting to device: $device")

            lifecycleScope.launch(Dispatchers.IO) {
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
            println("Connecting: $device")

            lifecycleScope.launch(Dispatchers.IO) {
                client.sendMessage(
                    host = device.host,
                    port = device.port,
                    message = "Ping from Android!"
                ) { resp ->
                    println("WS resp: $resp")
                }
            }
        }*/


        var themeSettings by mutableStateOf(
            ThemeSettings(
                isDarkMode = resources.configuration.isSystemInDarkTheme,
            ),
        )


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(),
                    themeViewModel.userSettings,
                ) { systemDark, uiState ->
                    ThemeSettings(
                        isDarkMode = uiState.shouldUseDarkTheme(systemDark),
                    )
                }
                    .onEach { themeSettings = it }
                    .map { it.isDarkMode }
                    .distinctUntilChanged()
                    .collect { darkTheme ->
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.auto(
                                lightScrim = android.graphics.Color.TRANSPARENT,
                                darkScrim = android.graphics.Color.TRANSPARENT,
                            ) { darkTheme },
                            navigationBarStyle = SystemBarStyle.auto(
                                lightScrim = lightScrim,
                                darkScrim = darkScrim,
                            ) { darkTheme },
                        )
                    }
            }
        }

        setContent {
            val windowSizeClass: WindowSizeClass = koinInject()
            DriveXcelAppTheme(
                darkTheme = themeSettings.isDarkMode,
                windowSizeClass = windowSizeClass
            ) {
//                DriveXcelApp()
                App()
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
//        mdnsManager.stop()
    }
}


@Composable
fun sdsd() {
    BackHandler { }
}

private data class ThemeSettings(
    val isDarkMode: Boolean
)

/**
 * Convenience wrapper for dark mode checking
 */
private val Configuration.isSystemInDarkTheme
    get() = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/**
 * Registers listener for configuration changes to retrieve whether system is in dark theme or not.
 * Immediately upon subscribing, it sends the current value and then registers listener for changes.
 */
private fun ComponentActivity.isSystemInDarkTheme() = callbackFlow {
    channel.trySend(resources.configuration.isSystemInDarkTheme)

    val listener = Consumer<Configuration> {
        channel.trySend(it.isSystemInDarkTheme)
    }

    addOnConfigurationChangedListener(listener)

    awaitClose { removeOnConfigurationChangedListener(listener) }
}
    .distinctUntilChanged()
    .conflate()

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)

private fun registerMdnsService(callback: (MdnsService.Device) -> Unit = {}) {
    CoroutineScope(Dispatchers.IO).launch {
        val mdns = MdnsService.Builder()
            .name("Android")
            .build()


        val devices = mutableSetOf<MdnsService.Device>()

        mdns.start { host, port, name ->
            val device = MdnsService.Device(host, port, name)
            if (devices.add(device)) {
                Log.d("discoverServices", "Found service at $host:$port :: $name")
                callback.invoke(device)
            }

        }


// Остановка
//    mdns.stop()
    }
}


interface DiscoveryListener {
    fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int)
    fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int)
    fun onDiscoveryStarted(serviceType: String?)
    fun onDiscoveryStopped(serviceType: String?)
    fun onServiceFound(serviceInfo: NsdServiceInfo?)
    fun onServiceLost(serviceInfo: NsdServiceInfo?)
}

private fun Activity.mdnsRegister() {
    val nsdManager = getSystemService(NSD_SERVICE) as NsdManager
    val SERVICE_TYPE = "_telnet._tcp."
    val resolveListener = object : NsdManager.ResolveListener {
        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {

        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            // do whatever I want with the resolved service
        }
    }

    val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(regType: String) {

        }

        override fun onServiceFound(service: NsdServiceInfo) {

            when {
                service.serviceType != SERVICE_TYPE -> {}

                service.serviceName.contains("NAD") ->
                    nsdManager.resolveService(service, resolveListener)
            }
        }

        override fun onServiceLost(service: NsdServiceInfo) {
//                if (service.serviceType != type) {
//
//                } else {
//                    serviceInfoSubject.onNext(Option.empty())
//                }
        }

        override fun onDiscoveryStopped(serviceType: String) {

        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            nsdManager.stopServiceDiscovery(this)
        }
    }

    nsdManager.discoverServices(
        SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener
    )
}

class KtorHttpPlatformProviderAndroid : KtorHttpPlatformProvider {

    override val client: HttpClient by lazy {
        HttpClient(OkHttp) {
            install(WebSockets)
        }
    }

    override fun createHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(WebSockets)
        }
    }
}

class MdnsManagerProviderAndroid() : MdnsManagerProvider {
    override fun provide(): MdnsManager {
        return MdnsManager(
            serviceName = android.os.Build.MODEL ?: "Android"
        )
    }
}

class LocalIpAddressAndroid(
    private val context: Context
) : LocalIpAddress {
    override fun getIp(): String? {
        val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ip = wm.connectionInfo.ipAddress
        return String.format(
            "%d.%d.%d.%d",
            ip and 0xff,
            ip shr 8 and 0xff,
            ip shr 16 and 0xff,
            ip shr 24 and 0xff
        )
    }
}


class CoroutineDispatcherProviderAndroid : CoroutineDispatcherProvider {
    override fun main(): CoroutineDispatcher {
        return Dispatchers.Main
    }
}

class MdnsServiceBuilderProviderAndroid : V2.MdnsServiceBuilderProvider {
    override fun provide(): V2.MdnsServiceBuilder {
        return V2.MdnsServiceBuilder().config {
            copy(
                name = android.os.Build.MODEL ?: "Android"
            )
        }
    }
}

class KtorClientProviderAndroid : V2.KtorClientProvider {

    override val client: HttpClient by lazy {
        HttpClient(OkHttp) {
            install(WebSockets)
        }
    }

}




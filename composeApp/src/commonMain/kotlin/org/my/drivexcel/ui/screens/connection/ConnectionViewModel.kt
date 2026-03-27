package org.my.drivexcel.ui.screens.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.my.drivexcel.KtorHttpPlatformProvider
import org.my.drivexcel.LocalIpAddress
import org.my.drivexcel.MdnsManagerProvider
import org.my.drivexcel.MdnsService
import org.my.drivexcel.data.DevicesRepository
import org.my.drivexcel.data.KtorWebSocketClientsManager
import org.my.drivexcel.data.KtorWebSocketServer
import org.my.drivexcel.requestConnection
import org.my.drivexcel.startWebSocketServer


class ConnectionViewModel(
    private val mdnsManagerProvider: MdnsManagerProvider,
    private val ktorHttpPlatformProvider: KtorHttpPlatformProvider,
    private val localIpAddress: LocalIpAddress,
    private val ktorWebSocketClientsManager: KtorWebSocketClientsManager,
    private val devicesRepository: DevicesRepository,
    private val ktorWebSocketServer: KtorWebSocketServer
) : ViewModel() {

    private val mdnsManager = mdnsManagerProvider.provide()

    private val _uiState = MutableStateFlow(ConnectionUiState())
    val uiState = _uiState.asStateFlow()

    // Колбэки на входящие запросы
    private val requestCallbacks = mutableMapOf<MdnsService.Device, suspend (Boolean) -> Unit>()

    // Храним открытые чаты с устройствами
    private val chatSessions = mutableMapOf<MdnsService.Device, ChatSession>()

    sealed interface ConnectionStatus {
        data object Idle : ConnectionStatus
        data object Connecting : ConnectionStatus
        data class Connected(val device: MdnsService.Device) : ConnectionStatus
        data class Rejected(val reason: String) : ConnectionStatus
    }

    fun updateDevices(devices: List<MdnsService.Device>) {
        _uiState.update {
            it.copy(
                devices = devices
            )
        }
    }

    init {
        /*viewModelScope.launch {
            devicesRepository.connectedDevices.collect { device ->
                _uiState.update { it.copy(
                    approvedDevices = it.approvedDevices += device
                ) }
            }

        }*/
        viewModelScope.launch {
            launch {
                devicesRepository.connectedDevices.collect { devices ->
                    _uiState.update { state ->
                        state.copy(
                            approvedDevices = devices   // просто подставляем весь список
                        )
                    }
                }
            }
            launch {
                ktorWebSocketServer.pendingServerHandshakes.collect { pendings ->
                    _uiState.update { state ->
                        state.copy(
                            pendingRequests = pendings.map { it.device }
                        )
                    }
                }
            }
            launch {
                ktorWebSocketClientsManager.pendingClients.collect { pendings ->
                    _uiState.update { state ->
                        state.copy(
                            pendingClients = pendings.map { it.device }
                        )
                    }
                }
            }

        }

    }


    // -------------------- WebSocket Server --------------------
    private fun startServer() {
        startWebSocketServer(port = 5000, mdnsManager = mdnsManager) { device, respond ->
            _uiState.update { it.copy(pendingRequests = it.pendingRequests + device) }
            requestCallbacks[device] = respond
        }
    }

    fun approve(device: MdnsService.Device) {
        viewModelScope.launch {
            ktorWebSocketServer.approve(device)
        }
        /*val callback = requestCallbacks.remove(device) ?: return

        viewModelScope.launch {
            // 1️⃣ Отправляем ответ device
            callback(true)

            // 2️⃣ Добавляем device в approvedDevices
            _uiState.update { state ->
                state.copy(
                    pendingRequests = state.pendingRequests - device,
                    approvedDevices = state.approvedDevices + device
                )
            }

            // 3️⃣ Автоматически открываем чат (singleton + reconnect)
            openChat(device)
        }*/
    }

    fun reject(device: MdnsService.Device) {
        viewModelScope.launch {
            ktorWebSocketServer.reject(device)
        }

        /*val callback = requestCallbacks.remove(device) ?: return

        viewModelScope.launch {
            callback(false)
            _uiState.update { state ->
                state.copy(
                    pendingRequests = state.pendingRequests - device
                )
            }
        }*/
    }


    // -------------------- Chat --------------------
    private val chatSessionsLock = Mutex()

    fun openChat(device: MdnsService.Device) {
        viewModelScope.launch {
            chatSessionsLock.withLock {
                // Проверяем, есть ли уже сессия
                if (chatSessions[device] != null) return@launch

                // Проверяем, что device одобрен
                if (!_uiState.value.approvedDevices.contains(device)) return@launch

                // Запускаем protected chat session с reconnect
                launchApprovedChatSession(device)
            }
        }
    }

    private fun launchApprovedChatSession(device: MdnsService.Device) = viewModelScope.launch {
        while (isActive) {
            try {
                println("Opening chat session with ${device.name}")

                ktorHttpPlatformProvider.client.webSocket(
                    host = device.host,
                    port = device.port,
                    path = "/chat"
                ) {
                    val session = ChatSession(this)

                    // Сохраняем сессию
                    chatSessionsLock.withLock {
                        chatSessions[device] = session
                        _uiState.update { it.copy(openChats = it.openChats + (device to true)) }
                    }

                    // Слушаем входящие сообщения
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            println("Message from ${device.name}: ${frame.readText()}")
                        }
                    }

                }
            } catch (e: Exception) {
                println("Chat session with ${device.name} failed: ${e.message}")
            } finally {
                chatSessionsLock.withLock {
                    chatSessions.remove(device)
                    _uiState.update { it.copy(openChats = it.openChats - device) }
                }
            }

            // Reconnect только для одобренных
            if (!_uiState.value.approvedDevices.contains(device)) break

            println("Reconnecting to ${device.name} in 2 seconds...")
            delay(2000)
        }
    }

    /*fun sendMessage(device: MdnsService.Device, message: String) {
        val session = chatSessions[device] ?: return
        viewModelScope.launch {
            session.sendMessage(message)
        }
    }*/

    fun sendMessageV2(device: MdnsService.Device, message: String) {
        viewModelScope.launch {
            devicesRepository.sendMessageTo(device, message)
        }
    }

    fun getChatSession(device: MdnsService.Device): ChatSession? = chatSessions[device]

    // -------------------- Connect to external device --------------------
    fun connectTo(device: MdnsService.Device) {
        viewModelScope.launch {
            _uiState.update { it.copy(connectionStatus = ConnectionStatus.Connecting) }

            val currentDevice =
                mdnsManager.getDevices().find { it.host == localIpAddress.getIp() } ?: return@launch


            val approved = ktorHttpPlatformProvider.client.requestConnection(
                fromDevice = currentDevice,
                toDevice = device
            )

            if (approved) {
                _uiState.update { state ->
                    state.copy(
                        approvedDevices = state.approvedDevices + device
                    )
                }

                // только инициатор открывает чат
                if (shouldStartChatWith(device)) {
                    openChat(device)
                }

                _uiState.update { it.copy(connectionStatus = ConnectionStatus.Connected(device)) }
            } else {
                _uiState.update { it.copy(connectionStatus = ConnectionStatus.Rejected("Rejected by ${device.name}")) }

            }

        }
    }

    fun connectToV2(device: MdnsService.Device) {
        ktorWebSocketClientsManager.connect(device)
    }


    private fun shouldStartChatWith(device: MdnsService.Device): Boolean {
        val myIp = localIpAddress.getIp() ?: return false
        return myIp < device.host
    }
}

class ChatSession(private val session: DefaultWebSocketSession) {
    suspend fun sendMessage(text: String) {
        session.send(text)
    }
}

data class ConnectionUiState(
    val devices: List<MdnsService.Device> = emptyList(),          // все найденные устройства
    val pendingRequests: List<MdnsService.Device> = emptyList(),  // запросы на handshake
    val pendingClients: List<MdnsService.Device> = emptyList(),  // запросы на handshake
    val approvedDevices: List<MdnsService.Device> = emptyList(),  // устройства, одобренные для чата
    val openChats: Map<MdnsService.Device, Boolean> = emptyMap(), // устройство -> открыт ли чат
    val connectionStatus: ConnectionViewModel.ConnectionStatus = ConnectionViewModel.ConnectionStatus.Idle,
    val availableDevicesV2: List<MdnsService.Device> = emptyList(), // доступные для подключения
    val connectedDeviceV2: List<MdnsService.Device> = emptyList(), // подключенные
)

/* private fun refreshDevicesPeriodically() {
        viewModelScope.launch {
            val myIp = localIpAddress.getIp()
            while (true) {
                val currentDevices = mdnsManager.getDevices().filter { it.host != myIp }

                _uiState.update { state ->
                    state.copy(
                        devices = currentDevices,
                        approvedDevices = state.approvedDevices
                            .filter { it.host != myIp }
                            .filter { approved -> currentDevices.any { it.host == approved.host } }
                    )
                }

                delay(2000) // раз в 2 сек, можно 1 сек
            }
        }
    }*/

/*
class ConnectionViewModel(
    private val mdnsManagerProvider: MdnsManagerProvider,
    private val ktorHttpPlatformProvider: KtorHttpPlatformProvider,
    private val localIpAddress: LocalIpAddress
) : ViewModel() {

    private val mdnsManager = mdnsManagerProvider.provide()

    private val _state = MutableStateFlow(ConnectionUiState())
    val state = _state.asStateFlow()

    // найденные устройства
    var devices by mutableStateOf<List<MdnsService.Device>>(emptyList())
        private set

    // входящие запросы на подключение
    var pendingRequests by mutableStateOf<List<String>>(emptyList())
        private set

    // результат подключения
    var connectionStatus by mutableStateOf<ConnectionStatus>(ConnectionStatus.Idle)
        private set

    sealed interface ConnectionStatus {
        data object Idle : ConnectionStatus
        data object Connecting : ConnectionStatus
        data class Connected(val deviceName: String) : ConnectionStatus
        data class Rejected(val reason: String) : ConnectionStatus
    }

    init {
        startMdns()
        startServer()
    }

    fun onDeviceFound(device: MdnsService.Device) {
        _state.update {
            it.copy(devices = it.devices + device)
        }
    }

    fun onIncomingRequest(device: MdnsService.Device, respond: suspend (Boolean) -> Unit) {
        _state.update {
            it.copy(pendingRequests = it.pendingRequests + device)
        }

        // запоминаем callback для будущего
        requestCallbacks[device] = respond
    }

    private val requestCallbacks = mutableMapOf<MdnsService.Device, suspend (Boolean) -> Unit>()

    fun approve(device: MdnsService.Device) {
        viewModelScope.launch {
            requestCallbacks[device]?.invoke(true)

            _state.update {
                it.copy(
                    pendingRequests = it.pendingRequests - device,
                    approved = it.approved + device
                )
            }

            openChat(device)
        }
    }

    fun reject(device: MdnsService.Device) {
        viewModelScope.launch {
            requestCallbacks[device]?.invoke(false)
            _state.update { it.copy(pendingRequests = it.pendingRequests - device) }
        }
    }

    private fun openChat(device: MdnsService.Device) {
        viewModelScope.launch {
            openChat(device.host, device.port) { incoming ->
                println("Chat message from ${device.name}: $incoming")
            }

            _state.update {
                it.copy(openChats = it.openChats + (device to true))
            }
        }
    }

    private fun startMdns() {
        val myIp = localIpAddress.getIp()

        mdnsManager.start { device ->
            devices = mdnsManager.getDevices().filter { it.host != myIp }
        }
    }

    private fun startServer() {
        startWebSocketServer(port = 5000) { from, respond ->
            pendingRequests = pendingRequests + from

            // ViewModel хранит "колбэк ответа"
            requestCallbacks[from] = respond
        }
    }

//    private val requestCallbacks = mutableMapOf<String, suspend (Boolean) -> Unit>()

    */
/*fun approveRequest(deviceName: String) {
        val cb = requestCallbacks[deviceName] ?: return

        viewModelScope.launch {
            cb(true)
            pendingRequests = pendingRequests - deviceName
        }
    }

    fun rejectRequest(deviceName: String) {
        val cb = requestCallbacks[deviceName] ?: return

        viewModelScope.launch {
            cb(false)
            pendingRequests = pendingRequests - deviceName
        }
    }*//*


    fun connectTo(device: MdnsService.Device, deviceName: String) {
        viewModelScope.launch {
            connectionStatus = ConnectionStatus.Connecting

            val approved = ktorHttpPlatformProvider.client.requestConnection(
                host = device.host,
                port = device.port,
                deviceName = deviceName
            )

            connectionStatus = if (approved)
                ConnectionStatus.Connected(device.name)
            else
                ConnectionStatus.Rejected("Rejected by ${device.name}")
        }
    }

    suspend fun openChat(
        host: String,
        port: Int,
        onIncoming: (String) -> Unit,
//        onReady: (ChatSession) -> Unit
    ) {
        ktorHttpPlatformProvider.client.webSocket(
            host = host,
            port = port,
            path = "/chat"
        ) {
            val session = ChatSession(this)
//            onReady(session) // UI теперь может отправлять сообщения

            val receiveJob = launch {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        onIncoming(frame.readText())
                    }
                }
            }

            receiveJob.join()
        }
    }
}

class ChatSession(
    val session: DefaultWebSocketSession
) {
    suspend fun sendMessage(text: String) {
        session.send(text)
    }
}

data class ConnectionUiState(
    val devices: List<MdnsService.Device> = emptyList(),         // найденные mDNS
    val pendingRequests: List<MdnsService.Device> = emptyList(), // запросы на handshake
    val approved: List<MdnsService.Device> = emptyList(),        // устройства с открытым чатом
    val openChats: Map<MdnsService.Device, Boolean> = emptyMap() // true = чат открыт
)
*/


package org.my.drivexcel.data

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.my.drivexcel.KtorHttpPlatformProvider
import org.my.drivexcel.LocalIpAddress
import org.my.drivexcel.MdnsService
import org.my.drivexcel.V2
import org.my.drivexcel.data.WsMessage.ChatMessage
import org.my.drivexcel.data.WsMessage.Handshake
import org.my.drivexcel.data.WsMessage.HandshakeApproval
import org.my.drivexcel.data.WsMessage.Ping
import org.my.drivexcel.data.WsMessage.Pong

class KtorWebSocketServer(
    private val localIpAddress: LocalIpAddress,
    private val mdnsManager: V2.MdnsManager

) {

    /*private val json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }*/

    private val _sessions = MutableStateFlow<List<ConnectedClient>>(emptyList())
    val sessions: StateFlow<List<ConnectedClient>> = _sessions.asStateFlow()

    private val _pendingServerHandshakes =
        MutableStateFlow<List<PendingServerHandshake>>(emptyList())
    val pendingServerHandshakes: StateFlow<List<PendingServerHandshake>> =
        _pendingServerHandshakes.asStateFlow()

    private val _serverPending = MutableStateFlow<ServerPending>(ServerPending.Idle)
    val serverPending: StateFlow<ServerPending> = _serverPending


    private fun serverDevice(): MdnsService.Device {
        return mdnsManager.getDevices().firstOrNull { it.host == localIpAddress.getIp() }
            ?.let { MdnsService.Device(it.host, it.port, it.name) }
            ?: MdnsService.Device.default()
    }

    /* fun start(port: Int) {
         embeddedServer(CIO, port = port) {
             install(WebSockets) {
                 pingPeriod = Duration.parse("15s") // встроенный ping
             }

             routing {
                 webSocket("/chat") {
                     println("Client connected")
 //                    send("Welcome!")

                     var lastActivity = System.currentTimeMillis()

                     // Лончем корутину для heartbeat проверки
                     val job = launch {
                         while (true) {
                             delay(5000)
                             val inactive = System.currentTimeMillis() - lastActivity > 10000
                             if (inactive) {
                                 println("Client inactive, closing socket")
                                 close(CloseReason(CloseReason.Codes.NORMAL, "Inactive"))
                                 break
                             }
                         }
                     }

                     try {
                         for (frame in incoming) {
                             val text = (frame as? Frame.Text)?.readText() ?: continue
                             val message = Json.decodeFromString<WsMessage>(text)

                             lastActivity = System.currentTimeMillis()

                             when (message) {
                                 is WsMessage.Handshake -> {
                                     println("Handshake from: ${message.senderDevice}")
                                     val reply = WsMessage.Handshake(serverDevice)
                                     send(Json.encodeToString(reply))
                                 }

                                 is WsMessage.ChatMessage -> {
                                     println("Chat msg: ${message.text}")
                                     val reply = WsMessage.ChatMessage("Echo: ${message.text}")
                                     send(Json.encodeToString(reply))
                                 }

                                 WsMessage.Ping -> {
                                     send(Json.encodeToString(WsMessage.Pong))
                                 }

                                 WsMessage.Pong -> {
                                     // просто обновляем lastActivity
                                 }
                             }
                         }
                     } finally {
                         job.cancel()
                         println("Client disconnected")
                     }
                 }
             }
         }.start(wait = false)
     }*/

    fun testStart(port: Int) {
        embeddedServer(CIO, port = port) {
            install(WebSockets)

            routing {
                webSocket("/chat") {
                    println("Server: client connected")
                    try {
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val text = frame.readText()
                                println("text: $text")
                                try {
                                    val message = Json.decodeFromString<WsMessage>(text)
                                    when (message) {
                                        is WsMessage.Handshake -> {
                                            println("Handshake from: ${message.senderDevice}")
                                            val reply = Handshake(serverDevice())

                                            /*  _sessions.value += ConnectedClient(
                                                  device = message.senderDevice,
                                                  session = this
                                              )  // сохраняем сессию*/

                                            _pendingServerHandshakes.value += PendingServerHandshake(
                                                device = message.senderDevice,
                                                session = this
                                            )

                                            send(Json.encodeToString(WsMessage.serializer(), reply))
                                        }

                                        is WsMessage.HandshakeApproval -> {

                                        }

                                        is WsMessage.ChatMessage -> {
                                            println("Chat msg: ${message.text}")
//                                            val reply =
//                                                WsMessage.ChatMessage("Echo: ${message.text}")
//                                            send(Json.encodeToString(WsMessage.serializer(),reply))
                                        }

                                        WsMessage.Ping -> {
                                            send(
                                                Json.encodeToString(
                                                    WsMessage.serializer(),
                                                    WsMessage.Pong
                                                )
                                            )
                                        }

                                        WsMessage.Pong -> {
                                            // просто обновляем lastActivity
                                        }

                                    }
                                } catch (e: Exception) {
                                    println("message decode error: ${e.toString()}")
                                }

                            }
                        }
                    } finally {
                        _sessions.value = _sessions.value.filterNot { it.session == this }
                        println("Server: client disconnected")
                    }
                }
            }
        }.start(wait = false)
    }

    suspend fun broadcastMessage(msg: String) {
        sessions.value.forEach { client ->
            client.session.send(msg)
        }
    }

    suspend fun sendToDevice(target: MdnsService.Device, msg: String) {
        val client =
            sessions.value.find { it.device.host == target.host && it.device.port == target.port }
                ?: return

        client.session.send(msg)
    }

    suspend fun approve(device: MdnsService.Device) {
        val pending = _pendingServerHandshakes.value.find { it.device == device } ?: return

        val approvalMsg = WsMessage.HandshakeApproval(true)
        pending.session.send(Json.encodeToString(WsMessage.serializer(), approvalMsg))

        _sessions.value += ConnectedClient(
            device = device,
            session = pending.session
        )

        _pendingServerHandshakes.value =
            _pendingServerHandshakes.value.filterNot { it.device == device }
    }

    suspend fun reject(device: MdnsService.Device) {
        val pending = _pendingServerHandshakes.value.find { it.device == device } ?: return

        val rejectionMsg = WsMessage.HandshakeApproval(false)
        pending.session.send(Json.encodeToString(WsMessage.serializer(), rejectionMsg))
//        pending.session.close(CloseReason(CloseReason.Codes.NORMAL, "Rejected by server"))

        _pendingServerHandshakes.value =
            _pendingServerHandshakes.value.filterNot { it.device == device }

    }

}

class KtorWebSocketClient(
    private val provider: KtorHttpPlatformProvider,
    private val scope: CoroutineScope,
    val device: MdnsService.Device,
    private val localIpAddress: LocalIpAddress,
    private val mdnsManager: V2.MdnsManager

) {

    /* private val json = Json {
         ignoreUnknownKeys = true
         classDiscriminator = "type"
     }
 */
    private val client = provider.client

    private var lastActivity = System.currentTimeMillis()

    private var session: DefaultClientWebSocketSession? = null

    private val _handshakeStatus = MutableStateFlow(HandshakeClientStatus.Idle)
    val handshakeStatus: StateFlow<HandshakeClientStatus> = _handshakeStatus

    private val _clientPending = MutableStateFlow<ClientPending>(ClientPending.Idle)
    val clientPending: StateFlow<ClientPending> = _clientPending


    fun clientDevice(): MdnsService.Device {
        return mdnsManager.getDevices().firstOrNull { it.host == localIpAddress.getIp() }
            ?.let { MdnsService.Device(it.host, it.port, it.name) }
            ?: MdnsService.Device.default()
    }

    /* fun start(host: String, port: Int, name: String, firstHandshake: Boolean = true) {
         scope.launch {
             client.webSocket(host = host, port = port, path = "/chat") {
                 println("Connected to server")

                 // 1️⃣ Отправляем handshake
                 if (firstHandshake) {
                     val handshake = WsMessage.Handshake(clientDevice)
                     send(Json.encodeToString(WsMessage.serializer(), handshake))
                 }

                 // 2️⃣ Лончим heartbeat: отправка ping каждые 5 секунд
                 val heartbeatJob = launch {
                     while (true) {
                         delay(5000)
                         send(Json.encodeToString(WsMessage.Ping))
                         val inactive = System.currentTimeMillis() - lastActivity > 10000
                         if (inactive) {
                             println("Server inactive, closing connection")
                             close(CloseReason(CloseReason.Codes.NORMAL, "Inactive"))
                             break
                         }
                     }
                 }

                 // 3️⃣ Лончим приём сообщений
                 val receiveJob = launch {
                     println("Starting to listen incoming...")
                     for (frame in incoming) {
                         println("RAW FRAME TYPE = ${frame.frameType}")

                         val text = (frame as? Frame.Text)?.readText() ?: continue
                         println("RAW TEXT = $text") // ← здесь
                         val message = Json.decodeFromString<WsMessage>(text)

                         lastActivity = System.currentTimeMillis()

                         when (message) {
                             is WsMessage.Handshake -> {
                                 println("Handshake response from server: ${message.senderDevice}")
                             }

                             is WsMessage.ChatMessage -> {
                                 println("Server says: ${message.text}")
                             }

                             WsMessage.Ping -> {
                                 send(Json.encodeToString(WsMessage.Pong))
                             }

                             WsMessage.Pong -> {
                                 // просто обновляем lastActivity
                             }
                         }

                     }
                     println("incoming closed")
                 }

                 // 4️⃣ Пример отправки сообщений от пользователя
                 launch {
                     val scanner = Scanner(System.`in`)
                     while (true) {
                         val line = scanner.nextLine()
                         if (line.lowercase() == "exit") break
                         val chatMsg = WsMessage.ChatMessage(line)
                         send(Json.encodeToString(chatMsg))
                     }
                     close()
                 }

                 // Ждём завершения
                 receiveJob.join()
                 heartbeatJob.cancel()
                 println("Connection closed")
             }
         }
     }*/

    fun testStart(
        firstHandshake: Boolean = true,
        updatePending: () -> Unit,
        onApprove: () -> Unit,
        onReject: () -> Unit,
        onDisconnect: () -> Unit
    ) {
        scope.launch {
            try {
                client.webSocket(host = device.host, port = device.port, path = "/chat") {
                    println("Client: connected")

                    if (firstHandshake) {
                        _handshakeStatus.value = HandshakeClientStatus.Pending
                        _clientPending.value = ClientPending.Pending
                        updatePending()
                        val handshake = WsMessage.Handshake(clientDevice())
                        send(Json.encodeToString(WsMessage.serializer(), handshake))
                    }

                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            println("Client received: $text")
                            try {
                                val message = Json.decodeFromString<WsMessage>(text)
                                when (message) {

                                    is Handshake -> {
                                        println("Handshake response to server: ${message.senderDevice}")
                                    }

                                    is HandshakeApproval -> {
                                        if (message.approved) {
                                            session = this  // сохраняем сессию
                                            _handshakeStatus.value = HandshakeClientStatus.Approved
                                            _clientPending.value = ClientPending.Approved(this)
                                            onApprove()
                                            updatePending()

                                        } else {
                                            _handshakeStatus.value = HandshakeClientStatus.Rejected
                                            _clientPending.value = ClientPending.Rejected

                                            this.close(
                                                CloseReason(
                                                    CloseReason.Codes.NORMAL,
                                                    "Rejected by server"
                                                )
                                            )
                                            onReject()
                                            updatePending()

                                        }
                                    }


                                    is ChatMessage -> {
                                        println("Server says: ${message.text}")
                                    }

                                    Ping -> {
                                        send(Json.encodeToString(Pong))
                                    }

                                    Pong -> {
                                        // просто обновляем lastActivity
                                    }

                                }
                            } catch (e: Exception) {
                                println("message decode error: ${e.toString()}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("client closed with error: ${e.toString()}")
            }

            _handshakeStatus.value = HandshakeClientStatus.Idle
            _clientPending.value = ClientPending.Idle
            updatePending()
            onDisconnect()
            println("Client: socket closed")
        }
    }

    // метод для отправки сообщений извне
    fun sendMessage(text: String) {
        scope.launch {
            session?.send(text)
        }
    }


}

class KtorWebSocketClientsManager(
    private val provider: KtorHttpPlatformProvider,
    private val localIpAddress: LocalIpAddress,
    private val mdnsManager: V2.MdnsManager
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _clients = MutableStateFlow<List<KtorWebSocketClient>>(emptyList())
    val clients: StateFlow<List<KtorWebSocketClient>> = _clients.asStateFlow()

    /** Flow всех ClientPending */
    @OptIn(ExperimentalCoroutinesApi::class)
    val clientsPending: StateFlow<List<ClientPending>> = _clients
        .flatMapLatest { clients ->
            combine(clients.map { it.clientPending }) { pendingArray ->
                pendingArray.toList()
            }
        }
        .stateIn(scope, SharingStarted.Lazily, emptyList())

    /** Flow Pending клиентов (только Pending) */
    @OptIn(ExperimentalCoroutinesApi::class)
    val pendingClients: StateFlow<List<KtorWebSocketClient>> = _clients
        .flatMapLatest { clients ->
            combine(clients.map { it.clientPending }) { pendingArray ->
//                println("pendingArray:")
//                pendingArray.forEach { print("pendingItem: $it") }
                clients.zip(pendingArray)
                    .filter { it.second is ClientPending.Pending || it.second is ClientPending.Approved }
                    .map { it.first }
            }
        }
        .stateIn(scope, SharingStarted.Lazily, emptyList())

    /** Flow Pending клиентов (только Pending) */
    @OptIn(ExperimentalCoroutinesApi::class)
    val approvedClients: StateFlow<List<KtorWebSocketClient>> = _clients
        .flatMapLatest { clients ->
            combine(clients.map { it.clientPending }) { pendingArray ->
//                println("pendingArray:")
//                pendingArray.forEach { print("pendingItem: $it") }
                clients.zip(pendingArray)
                    .filter { it.second is ClientPending.Approved }
                    .map { it.first }
            }
        }
        .stateIn(scope, SharingStarted.Lazily, emptyList())

    init {
        println("KtorWebSocketClientsManager init: $this")
    }

    fun connect(serverDevice: MdnsService.Device) {
        val client = KtorWebSocketClient(provider, scope, serverDevice, localIpAddress, mdnsManager)
        _clients.value += client

        client.testStart(
            updatePending = {  },
            onApprove = {
//                _clients.value += client
            },
            onReject = {
//                _clients.value -= client
            },
            onDisconnect = { _clients.value -= client }
        )
    }


    fun sendToDevice(target: MdnsService.Device, msg: String) {
        val client =
            clients.value.find { it.device.host == target.host && it.device.port == target.port }
                ?: return

        client.sendMessage(msg)
    }

    fun closeAll() {
        scope.cancel()
        _clients.value = emptyList()

    }

    fun isConnected(device: MdnsService.Device): Boolean {
        return _clients.value.any { it.device.host == device.host && it.device.port == device.port }
    }
}

class DevicesRepository(
    private val mdnsManager: V2.MdnsManager,
    private val clientsManager: KtorWebSocketClientsManager,
    private val ktorWebSocketServer: KtorWebSocketServer,
    private val localIpAddress: LocalIpAddress

) {

    init {
        println("DevicesRepository init: $this")
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Поток доступных для подключения устройств
    /* val availableDevices: StateFlow<List<MdnsService.Device>> = combine(
         mdnsManager.devices,       // найденные через mDNS
         clientsManager.clients,     // уже подключенные клиенты
         ktorWebSocketServer.sessions
     ) { foundDevices, connectedClients, serverSessions ->

         val connectedHosts = connectedClients.map { it.device.host }.toSet()
         val myHost = localIpAddress.getIp()

         // Фильтруем найденные устройства
         foundDevices.map {
             MdnsService.Device(
                 host = it.host,
                 port = it.port,
                 name = it.name
             )
         }.filter { device ->
             device.host != myHost && device.host !in connectedHosts
         }

     }.stateIn(scope, SharingStarted.Lazily, emptyList())
 */

    val availableDevices: StateFlow<List<MdnsService.Device>> = combine(
        mdnsManager.devices,         // найденные через mDNS
        clientsManager.approvedClients,      // клиентские подключения
        ktorWebSocketServer.sessions // серверные подключения
    ) { foundDevices, clientList, serverSessions ->

        val connectedHosts = buildSet {
            addAll(clientList
                .map { it.device.host })
            addAll(serverSessions.map { it.device.host })
        }

        val myHost = localIpAddress.getIp()

        foundDevices
            .map {
                MdnsService.Device(
                    host = it.host,
                    port = it.port,
                    name = it.name
                )
            }
            .filter { device ->
                device.host != myHost && device.host !in connectedHosts
            }

    }.stateIn(scope, SharingStarted.Lazily, emptyList())

    /* val connectedDevices: StateFlow<List<MdnsService.Device>> =
         combine(
             clientsManager.clients,
             ktorWebSocketServer.sessions
         ) { clientList, serverSessions ->

             val fromClients = clientList.map { it.device }
             val fromServer = serverSessions.map { it.device }

             (fromClients + fromServer)
         }.stateIn(scope, SharingStarted.Lazily, emptyList())
 */

    val connectedDevices: StateFlow<List<MdnsService.Device>> =
        combine(
            clientsManager.approvedClients,
            ktorWebSocketServer.sessions
        ) { clientList, serverSessions ->

            val fromClients = clientList
//                .filter { it.clientPending.value is ClientPending.Approved }
                .map { it.device }

            println("connectedDevices: fromClients: $fromClients")

            val fromServer = serverSessions.map { it.device }

            fromClients + fromServer
        }.stateIn(scope, SharingStarted.Lazily, emptyList())

    suspend fun sendMessageTo(device: MdnsService.Device, text: String) {
        // 1 💡 Если мы клиент и подключились сами — отправляем через клиент
        val client = clientsManager.clients.value.find {
            it.device.host == device.host && it.device.port == device.port
        }
        if (client != null) {
            client.sendMessage(
                Json.encodeToString(
                    WsMessage.serializer(),
                    WsMessage.ChatMessage(text)
                )
            )
            return
        }

        // 2 💡 Если устройство подключилось к нам — отправляем через сервер
        val serverClient = ktorWebSocketServer.sessions.value.find {
            it.device.host == device.host && it.device.port == device.port
        }
        if (serverClient != null) {
            serverClient.session.send(
                Json.encodeToString(
                    WsMessage.serializer(),
                    WsMessage.ChatMessage(text)
                )
            )
            return
        }

        println("⚠ Device not connected: $device")
    }

}

@Serializable
sealed class WsMessage {

    @Serializable
    @SerialName("handshake")
    data class Handshake(val senderDevice: MdnsService.Device) : WsMessage()

    @Serializable
    @SerialName("handshake_approval")
    data class HandshakeApproval(val approved: Boolean) : WsMessage()

    @Serializable
    @SerialName("chat_message")
    data class ChatMessage(val text: String) : WsMessage()

    @Serializable
    @SerialName("ping")
    data object Ping : WsMessage()

    @Serializable
    @SerialName("pong")
    data object Pong : WsMessage()

}

data class ConnectedClient(
    val device: MdnsService.Device,
    val session: DefaultWebSocketServerSession
)

data class PendingServerHandshake(
    val device: MdnsService.Device,
    val session: DefaultWebSocketServerSession
)

data class PendingClientHandshake(
    val device: MdnsService.Device,
    val session: DefaultClientWebSocketSession
)

enum class HandshakeClientStatus {
    Idle,
    Pending,
    Approved,
    Rejected
}

sealed interface ClientPending {
    object Idle : ClientPending
    data class Approved(
        val session: DefaultClientWebSocketSession
    ) : ClientPending

    object Rejected : ClientPending
    object Pending : ClientPending
}

sealed interface ServerPending {
    object Idle : ServerPending
    data class Approved(
        val device: MdnsService.Device,
        val session: DefaultWebSocketServerSession
    ) : ServerPending

    object Rejected : ServerPending
    object Pending : ServerPending
}
/*
data class MdnsDevice(
    val host: String,
    val port: Int,
    val name: String
) {
    companion object {
        fun default() = MdnsDevice(
            host = "192.168.0.100",
            port = 5000,
            name = "Sample device"
        )
    }
}*/

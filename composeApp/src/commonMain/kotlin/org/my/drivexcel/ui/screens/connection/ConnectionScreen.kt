package org.my.drivexcel.ui.screens.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import drivexcel.composeapp.generated.resources.Res
import drivexcel.composeapp.generated.resources.ic_arrow_back
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.my.drivexcel.MdnsService
import org.my.drivexcel.WebSocketsViewModel
import org.my.drivexcel.data.DevicesRepository
import org.my.drivexcel.platform.utils.BackHandlerProvider
import org.my.drivexcel.platform.utils.WindowSize
import org.my.drivexcel.platform.utils.WindowSizeClassPreview
import org.my.drivexcel.platform.utils.isWide
import org.my.drivexcel.theme.DriveXcelAppTheme
import org.my.drivexcel.theme.PhonePreview
import org.my.drivexcel.ui.screens.connection.components.MdnsDeviceConnectItem
import org.my.drivexcel.ui.screens.connection.components.MdnsDeviceResponseItem

@Composable
fun ConnectionRoute(
    viewModel: ConnectionViewModel = koinViewModel(),
    webSocketsViewModel: WebSocketsViewModel,
    backHandlerProvider: BackHandlerProvider = koinInject(),
    devicesRepository: DevicesRepository = koinInject(),
    windowSize: WindowSize,
    onBackPressed: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
//    val devices by webSocketsViewModel.devices.collectAsState()
    val devices by devicesRepository.availableDevices.collectAsState()

    LaunchedEffect(devices) {
        viewModel.updateDevices(devices.map {
            MdnsService.Device(
                host = it.host,
                port = it.port,
                name = it.name
            )
        })
    }

    backHandlerProvider.BackHandler {
        onBackPressed()
    }

    ConnectionScreen(
        uiState = uiState,
        windowSize = windowSize,
        devices = uiState.devices,
        pending = uiState.pendingRequests,
        pendingClients = uiState.pendingClients,
        status = uiState.connectionStatus,
        connectTo = viewModel::connectToV2,
        approveRequest = viewModel::approve,
        rejectRequest = viewModel::reject,
        sendMessage = viewModel::sendMessageV2,
        onBackPressed = onBackPressed
    )


}

@Composable
private fun ConnectionScreen(
    uiState: ConnectionUiState,
    windowSize: WindowSize,
    devices: List<MdnsService.Device>,
    pending: List<MdnsService.Device>,
    pendingClients: List<MdnsService.Device>,
    status: ConnectionViewModel.ConnectionStatus,
    connectTo: (MdnsService.Device) -> Unit,
    approveRequest: (MdnsService.Device) -> Unit,
    rejectRequest: (MdnsService.Device) -> Unit,
    sendMessage: (MdnsService.Device, String) -> Unit,
    onBackPressed: () -> Unit
) {

    ConnectionWrapper(
        windowSize = windowSize,
        onBackPressed = onBackPressed,
        content = { innerPadding ->
            ConnectionContent(
                innerPadding = innerPadding,
                uiState = uiState,
                devices = devices,
                pending = pending,
                pendingClients = pendingClients,
                status = status,
                connectTo = connectTo,
                approveRequest = approveRequest,
                rejectRequest = rejectRequest,
                sendMessage = sendMessage

            )
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectionWrapper(
    windowSize: WindowSize,
    onBackPressed: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {

    val isWide = windowSize.isWide

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Подключения"
                    )
                },
                navigationIcon = {
                    if (!isWide) {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_arrow_back),
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        content(
            innerPadding
        )
    }
}

@Composable
private fun ConnectionContent(
    innerPadding: PaddingValues,
    uiState: ConnectionUiState,
    devices: List<MdnsService.Device>,
    pending: List<MdnsService.Device>,
    pendingClients: List<MdnsService.Device>,
    status: ConnectionViewModel.ConnectionStatus,
    connectTo: (MdnsService.Device) -> Unit,
    approveRequest: (MdnsService.Device) -> Unit,
    rejectRequest: (MdnsService.Device) -> Unit,
    sendMessage: (MdnsService.Device, String) -> Unit
) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {

        ConnectionDevicesSection(
            devices = devices,
            pendingClients = pendingClients,
            connectTo = connectTo,
            sendMessage = sendMessage
        )
        PendingDevicesSection(
            pendings = pending,
            approve = approveRequest,
            reject = rejectRequest
        )

        LazyColumn {
            item {
                Text("Approved")
            }
            items(uiState.approvedDevices) { device ->
                Column {
                    Text(text = device.toString())
                    Button(
                        onClick = {
                            sendMessage(
                                device,
                                "Test message from initiator: $device"
                            )
                        }
                    ) {
                        Text("Send message")
                    }
                }
            }

        }

        /*Text("Доступные устройства", fontSize = 22.sp)

        devices.forEach { device ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${device.name} (${device.host})")

                Button(onClick = {
                    connectTo(device)
                }) {
                    Text("Подключиться")
                }
            }
        }

        Spacer(Modifier.height(24.dp))*/

        /*if (pending.isNotEmpty()) {
            Text("Входящие запросы", fontSize = 22.sp)
        }

        pending.forEach { from ->
            Column(
                Modifier.fillMaxWidth(),

                ) {
                Text("Запрос от: $from")

                Row {
                    Button(onClick = { approveRequest(from) }) { Text("Принять") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { rejectRequest(from) }) { Text("Отклонить") }
                }
            }
        }*/

        Spacer(Modifier.height(24.dp))

        when (status) {
            is ConnectionViewModel.ConnectionStatus.Connecting ->
                Text("Подключение...")

            is ConnectionViewModel.ConnectionStatus.Connected ->
                Text("Подключено к ${status.device}")

            is ConnectionViewModel.ConnectionStatus.Rejected ->
                Text("Отклонено: ${status.reason}", color = Color.Red)

            else -> {}
        }
    }

}

@Composable
private fun ColumnScope.ConnectionDevicesSection(
    devices: List<MdnsService.Device>,
    pendingClients: List<MdnsService.Device>,
    connectTo: (MdnsService.Device) -> Unit,
    sendMessage: (MdnsService.Device, String) -> Unit

) {
    SectionTitle(
        text = "Доступные устройства"
    )

    if (devices.isEmpty()) {
        CircularProgressIndicator()
    } else {
        LazyColumn {
            itemsIndexed(devices) { index, device ->
                MdnsDeviceConnectItem(
                    device = device,
                    onClick = { connectTo(device) },
                    pendingClient = { pendingClients.contains(device) },
                    sendMessage = sendMessage
                )
            }
        }
    }


}

@Composable
private fun ColumnScope.PendingDevicesSection(
    pendings: List<MdnsService.Device>,
    approve: (MdnsService.Device) -> Unit,
    reject: (MdnsService.Device) -> Unit,
) {
    SectionTitle(
        text = "Запросы на подключение"
    )

    LazyColumn {
        itemsIndexed(pendings) { index, device ->
           /* MdnsDeviceConnectItem(
                device = device,
                onClick = { connectTo(device) },
                pendingClient = { pendingClients.contains(device) },
                sendMessage = sendMessage
            )*/
            MdnsDeviceResponseItem(
                device = device,
                onApprove = { approve(device) },
                onReject = { reject(device) }
            )
        }
    }
}

@Composable
private fun SectionTitle(
    text: String
) = Text(
    text = text,
    style = MaterialTheme.typography.titleLarge
)

@Composable
private fun ConnectionScreenDefault(

) = ConnectionScreen(
    windowSize = WindowSize.Compact,
    uiState = ConnectionUiState(),
    devices = emptyList(),
    pending = emptyList(),
    pendingClients = emptyList(),
    status = ConnectionViewModel.ConnectionStatus.Idle,
    connectTo = { _ -> },
    approveRequest = {},
    rejectRequest = {},
    sendMessage = { _, _ -> },
    onBackPressed = {}
)

@PhonePreview
@Composable
private fun ConnectionScreenPreviewNightPhone() = DriveXcelAppTheme(
    darkTheme = true,
    windowSizeClass = WindowSizeClassPreview()
) {
    ConnectionScreenDefault()
}

@PhonePreview
@Composable
private fun SConnectionScreenPreviewLightPhone() = DriveXcelAppTheme(
    darkTheme = false,
    windowSizeClass = WindowSizeClassPreview()

) {
    ConnectionScreenDefault()
}
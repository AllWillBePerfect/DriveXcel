package org.my.drivexcel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.my.drivexcel.data.KtorWebSocketClientsManager
import org.my.drivexcel.data.KtorWebSocketServer

/**
 * mDNS + Ktor WebSockets
 */
class WebSocketsViewModel(
    private val mdnsManager: V2.MdnsManager,
    private val ktorWebSocketClientsManager: KtorWebSocketClientsManager,
    private val ktorWebSocketServer: KtorWebSocketServer
) : ViewModel() {

    val devices = mdnsManager.devices

    init {
//        mdnsManager.start {  }
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                ktorWebSocketServer.testStart(port = 5000)
//            }
//        }

    }

    override fun onCleared() {
        super.onCleared()
//        mdnsManager.stop()
    }

}
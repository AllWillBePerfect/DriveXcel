package org.my.drivexcel

import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.my.drivexcel.platform.utils.AppLogger
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo
import javax.jmdns.ServiceListener

object V2 {

    class MdnsService(
        private val mdnsConfig: MdnsConfig,
    ) {
        private var jmdns: JmDNS? = null
        private var listener: ServiceListener? = null


        fun start(
            onDeviceFound: (foundedDevice: Device) -> Unit,
            onDeviceRemoved: (String) -> Unit

        ) {

            if (jmdns != null) return

            val addr = getBindAddress()
            jmdns = JmDNS.create(addr)

            // Регистрируем сервис
            val serviceInfo = ServiceInfo.create(
                mdnsConfig.serviceType,
                mdnsConfig.name,
                mdnsConfig.port,
                mdnsConfig.description
            )
            jmdns?.registerService(serviceInfo)

            // Слушаем сервисы других устройств
            listener = object : ServiceListener {
                override fun serviceAdded(event: ServiceEvent?) {
                    // Запрашиваем полное info
                    jmdns?.requestServiceInfo(mdnsConfig.serviceType, event?.name)
                }

                override fun serviceRemoved(event: ServiceEvent?) {
                    val name = event?.name ?: return
                    // Передаем только имя, остальное можно оставить пустым
                    onDeviceRemoved(name)
                }

                override fun serviceResolved(event: ServiceEvent?) {
                    val info = event?.info ?: return
                    val host = info.hostAddresses.firstOrNull() ?: return
                    val foundedDevice = Device(
                        host = host,
                        port = info.port,
                        name = info.name
                    )
                    onDeviceFound(foundedDevice)
                }
            }
            jmdns?.addServiceListener(mdnsConfig.serviceType, listener)
        }

        // Остановка сервиса
        fun stop() {
            jmdns?.removeServiceListener(mdnsConfig.serviceType, listener)
            jmdns?.unregisterAllServices()
            jmdns?.close()
        }

        private fun getBindAddress(): InetAddress {
            // Android: wlan0
            getWifiInetAddress()?.let { return it }

//            logger.i("MdnsService", "Wi-fi not found :(")

            // Desktop: нормальный local host
            return InetAddress.getLocalHost()
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


    }

    // Builder для удобного создания
    class MdnsServiceBuilder(

    ) {

        private var mdnsConfig: MdnsConfig = MdnsConfig(
            serviceType = "_drivexcel._tcp.local.",
            name = "Device",
            port = 5000,
            description = "KMP Service for communicate between same devices"
        )

        fun serviceType(type: String) =
            apply { this.mdnsConfig = this.mdnsConfig.copy(serviceType = type) }

        fun name(name: String) = apply { this.mdnsConfig = this.mdnsConfig.copy(name = name) }
        fun port(port: Int) = apply { this.mdnsConfig = this.mdnsConfig.copy(port = port) }
        fun description(description: String) =
            apply { this.mdnsConfig = this.mdnsConfig.copy(description = description) }

        fun data(data: MdnsConfig) = apply { this.mdnsConfig = data }

        fun config(block: (MdnsConfig.() -> MdnsConfig)) = apply {
            this.mdnsConfig = block(this.mdnsConfig)
        }

        fun build(): MdnsService {
            return MdnsService(mdnsConfig)
        }
    }

    class MdnsManager(
        private val mdnsService: MdnsService,
        private val logger: AppLogger
    ) {

        private val _devices = MutableStateFlow<Set<Device>>(emptySet())
        val devices = _devices.asStateFlow()

        private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        /**
         * Запускает публикацию сервиса и поиск других устройств
         */
        fun start(callback: (Device) -> Unit = {}) {

            scope.launch(Dispatchers.IO) {
                mdnsService.start(
                    onDeviceFound = { foundedDevice ->

                        val added = _devices.updateAndGet { current ->
                            if (foundedDevice !in current) {
                                logger.i("MdnsManager", "Found mDNS device: $foundedDevice")
                                current + foundedDevice
                            } else {
                                current
                            }
                        }.contains(foundedDevice)

                      /*  if (added) {
                            launch(Dispatchers.Main) { onDeviceFoundCallback(device) }
                        }*/
// если устройство было добавлено впервые
//                    if (added) {
//                        logger.i("MdnsManager", "Found mDNS device: $foundedDevice")
//
//                        // callback вызываем на главном потоке, если нужно UI
//                        launch(Dispatchers.Main) {
//                            callback(foundedDevice)
//                        }
//                    }



                        /* if (_devices.add(foundedDevice)) {
                             logger.i("MdnsManager", "Found mDNS device: $foundedDevice")
                             // callback вызываем на главном потоке, если нужно UI
                             launch(Dispatchers.Main) {
                                 callback(foundedDevice)
                             }
                         }*/
                    },
                    onDeviceRemoved = { deviceName ->
                        val removed = _devices.updateAndGet { cur ->
                            cur.filterNot { it.name == deviceName }.toSet()
                        }

                     /*   if (device.name !in removed.map { it.name }) {
                            launch(Dispatchers.Main) { onDeviceRemovedCallback(device) }
                        }*/
                    }
                )

            }

        }

        /**
         * Получить список всех найденных устройств
         */
        fun getDevices(): List<Device> = _devices.value.toList()

        /**
         * Останавливает публикацию и поиск
         */
        fun stop() {
            scope.cancel()
            mdnsService.stop()
            _devices.value = emptySet()
            logger.i("MdnsManager", "mDNS service stopped")
        }

    }

    data class MdnsConfig(
        val serviceType: String,
        val name: String,
        val port: Int,
        val description: String
    )

    interface MdnsServiceBuilderProvider {
        fun provide(): MdnsServiceBuilder
    }

    @Serializable
    data class Device(val host: String, val port: Int, val name: String)

    interface KtorClientProvider {
        val client: HttpClient
    }

}

interface CoroutineDispatcherProvider {
    fun main(): CoroutineDispatcher
}


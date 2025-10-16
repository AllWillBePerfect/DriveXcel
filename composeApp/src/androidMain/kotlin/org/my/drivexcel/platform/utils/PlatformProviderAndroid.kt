package org.my.drivexcel.platform.utils

class PlatformProviderAndroid : PlatformProvider {
    override fun currentPlatform(): PlatformProvider.Platform = PlatformProvider.Platform.Android
}
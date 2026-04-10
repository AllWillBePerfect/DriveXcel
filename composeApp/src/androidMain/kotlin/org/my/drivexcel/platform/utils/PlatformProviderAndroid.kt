package org.my.drivexcel.platform.utils

import org.my.drivexcel.platform.PlatformProvider

class PlatformProviderAndroid : PlatformProvider {
    override fun currentPlatform(): PlatformProvider.Platform = PlatformProvider.Platform.Android
}
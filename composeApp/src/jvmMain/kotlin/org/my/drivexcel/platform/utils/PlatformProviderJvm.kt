package org.my.drivexcel.platform.utils

class PlatformProviderJvm : PlatformProvider {
    override fun currentPlatform(): PlatformProvider.Platform = PlatformProvider.Platform.Jvm
}
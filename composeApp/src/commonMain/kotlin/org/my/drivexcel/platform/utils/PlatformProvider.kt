package org.my.drivexcel.platform.utils

interface PlatformProvider {

    fun currentPlatform(): Platform

    enum class Platform {
        Android, Jvm
    }
}
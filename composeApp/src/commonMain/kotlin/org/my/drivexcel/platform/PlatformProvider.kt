package org.my.drivexcel.platform

interface PlatformProvider {

    fun currentPlatform(): Platform

    enum class Platform {
        Android, Jvm
    }
}
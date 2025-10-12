package org.my.drivexcel

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
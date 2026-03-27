package org.my.drivexcel.data.v3

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface TimeProvider {

    @OptIn(ExperimentalTime::class)
    fun now(): Instant

    class Impl() : TimeProvider {
        @OptIn(ExperimentalTime::class)
        override fun now(): Instant {
            return Clock.System.now()
        }
    }
}
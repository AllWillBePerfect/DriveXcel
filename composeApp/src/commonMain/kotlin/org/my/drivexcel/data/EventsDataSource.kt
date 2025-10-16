package org.my.drivexcel.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

interface EventsDataSource {

    val events: Flow<List<Int>>

    fun update()

    class Impl() : EventsDataSource {
        private val _events = MutableStateFlow<List<Int>>(emptyList())
        override val events: Flow<List<Int>> get() = _events


        override fun update() {
            _events.update {
                (1..50).map { Random.nextInt(1,100) }.toSet().toList()
            }
        }

    }
}


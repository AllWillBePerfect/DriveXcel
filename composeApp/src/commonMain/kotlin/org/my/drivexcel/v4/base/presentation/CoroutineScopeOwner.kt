package org.my.drivexcel.v4.base.presentation

import kotlinx.coroutines.CoroutineScope

interface CoroutineScopeOwner {
    val coroutineScope: CoroutineScope
}
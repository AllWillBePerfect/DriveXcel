package org.my.drivexcel.platform.utils

import androidx.compose.runtime.Composable
import org.my.drivexcel.platform.BackHandlerProvider

class BackHandlerProviderJvm : BackHandlerProvider {
    @Composable
    override fun BackHandler(enabled: Boolean, onBack: () -> Unit) {

    }
}
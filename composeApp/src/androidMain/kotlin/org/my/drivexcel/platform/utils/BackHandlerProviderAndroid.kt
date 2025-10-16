package org.my.drivexcel.platform.utils

import androidx.compose.runtime.Composable

class BackHandlerProviderAndroid : BackHandlerProvider {
    @Composable
    override fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
        androidx.activity.compose.BackHandler(
            enabled = enabled,
            onBack = onBack
        )
    }
}
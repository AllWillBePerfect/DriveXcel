package org.my.drivexcel.v4.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsRoute() {

    SettingsScreen()
}

@Composable
private fun SettingsScreen() {
    SettingsWrapper(
        content = { innerPadding ->
            SettingsContent(
                innerPadding = innerPadding
            )
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsWrapper(
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Настройки"
                    )
                }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun SettingsContent(
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier.padding(innerPadding)
    ) {
        Text("Настройки")
    }

}
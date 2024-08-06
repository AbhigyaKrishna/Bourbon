package me.abhigya.bourbon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.abhigya.bourbon.core.ui.router.RouterScreen
import me.abhigya.bourbon.core.utils.theme.BourbonTheme

@Composable
fun App() {
    BourbonTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            RouterScreen()
        }
    }
}
package me.abhigya.bourbon

import androidx.compose.runtime.Composable
import me.abhigya.bourbon.core.ui.router.RouterScreen
import me.abhigya.bourbon.core.utils.theme.BourbonTheme

@Composable
fun App() {
    BourbonTheme {
        RouterScreen()
    }
}
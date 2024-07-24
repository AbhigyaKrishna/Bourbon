package me.abhigya.bourbon

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.abhigya.bourbon.ui.login.LoginScreen
import me.abhigya.bourbon.core.utils.theme.BourbonTheme

@Preview(showBackground = true)
@Composable
fun App() {
    BourbonTheme {
        LoginScreen()
    }
}
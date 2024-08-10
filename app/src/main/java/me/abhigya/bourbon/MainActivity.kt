package me.abhigya.bourbon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import me.abhigya.bourbon.core.ui.splash.SplashHomeViewModel
import me.abhigya.bourbon.core.utils.ProvideDisplayInsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        splash.setKeepOnScreenCondition { SplashHomeViewModel.splashState.value }

        setContent {
            ProvideDisplayInsets {
                App()
            }
        }
    }
}
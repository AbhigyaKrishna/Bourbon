package me.abhigya.bourbon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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

        val splashVm: SplashHomeViewModel by viewModels()
        splash.setKeepOnScreenCondition { splashVm.splashState.value }

        setContent {
            ProvideDisplayInsets {
                App()
            }
        }
    }
}
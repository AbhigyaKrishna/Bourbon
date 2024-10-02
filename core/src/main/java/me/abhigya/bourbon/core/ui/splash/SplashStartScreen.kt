package me.abhigya.bourbon.core.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.abhigya.bourbon.core.R

object SplashStartScreen : SplashScreen() {

    @Composable
    override fun Content() {
        Text(text = stringResource(R.string.splash_screen_prepare_yourself))
    }

}
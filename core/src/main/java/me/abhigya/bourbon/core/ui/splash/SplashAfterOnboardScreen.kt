package me.abhigya.bourbon.core.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import kotlinx.coroutines.delay
import me.abhigya.bourbon.core.ui.router.LocalRouter
import me.abhigya.bourbon.core.ui.router.RoutePath

object SplashAfterOnboardScreen : SplashScreen() {

    @Composable
    override fun Content() {
        val coroutine = rememberCoroutineScope()
        val router = LocalRouter.current
        LaunchedEffect(coroutine) {
            delay(2000)
            router.trySend(RouterContract.Inputs.RestoreBackstack(listOf(RoutePath.HOME.directions().build())))
        }

        Text(text = "You are all set",)
    }

}
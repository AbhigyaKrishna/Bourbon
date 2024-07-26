package me.abhigya.bourbon.core.utils.features.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.copperleaf.ballast.navigation.routing.Backstack
import com.copperleaf.ballast.navigation.routing.renderCurrentDestination
import me.abhigya.bourbon.core.utils.features.auth.LoginScreen

@Composable
fun RouterScreen() {
    val coroutine = rememberCoroutineScope()
    val router = remember(coroutine) {
        Router(coroutine, RouteScreen.LOGIN)
    }
    val routerState: Backstack<RouteScreen> by router.observeStates().collectAsState()

    routerState.renderCurrentDestination(
        route = {
            when (it) {
                RouteScreen.LOGIN -> LoginScreen()
                else -> TODO()
            }
        },
        notFound = { }
    )
}
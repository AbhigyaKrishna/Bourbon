package me.abhigya.bourbon.core.ui.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.copperleaf.ballast.navigation.routing.Backstack
import com.copperleaf.ballast.navigation.routing.renderCurrentDestination
import me.abhigya.bourbon.core.ui.auth.AuthScreen
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

object RouterScreen : KoinComponent {

    @Composable
    operator fun invoke() {
        val coroutine = rememberCoroutineScope()
        val viewModel: RouterViewModel = remember(coroutine) { get { parametersOf(coroutine, RouteScreen.AUTH) } }
        val routerState: Backstack<RouteScreen> by viewModel.observeStates().collectAsState()

        routerState.renderCurrentDestination(
            route = {
                when (it) {
                    RouteScreen.AUTH -> AuthScreen()
                    else -> TODO()
                }
            },
            notFound = { }
        )
    }
}
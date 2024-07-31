package me.abhigya.bourbon.core.ui.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import com.copperleaf.ballast.navigation.routing.Backstack
import com.copperleaf.ballast.navigation.routing.renderCurrentDestination
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import me.abhigya.bourbon.core.ui.auth.AuthScreen
import me.abhigya.bourbon.core.ui.onboarding.OnBoardingScreen
import me.abhigya.bourbon.domain.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

object RouterScreen : KoinComponent {

    private val isLoggedIn = runBlocking { get<UserRepository>().isLoggedIn().single() }

    @Composable
    operator fun invoke() {
        val coroutine = rememberCoroutineScope()
        val viewModel: RouterViewModel = remember(coroutine) { get { parametersOf(coroutine, if (isLoggedIn) RoutePath.HOME else RoutePath.AUTH) } }
        val routerState: Backstack<RoutePath> by viewModel.observeStates().collectAsState()

        CompositionLocalProvider(LocalRouter provides viewModel) {
            routerState.renderCurrentDestination(
                route = {
                    when (it) {
                        RoutePath.AUTH -> AuthScreen()
                        RoutePath.ONBOARDING -> OnBoardingScreen()
                        else -> TODO()
                    }
                },
                notFound = { }
            )
        }
    }
}

val LocalRouter = staticCompositionLocalOf<RouterViewModel> {
    error("Router not provided")
}
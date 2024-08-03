package me.abhigya.bourbon.core.ui.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.copperleaf.ballast.navigation.routing.Backstack
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.routing.renderCurrentDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.single
import me.abhigya.bourbon.core.ui.AppScreen
import me.abhigya.bourbon.core.ui.auth.AuthScreen
import me.abhigya.bourbon.core.ui.home.HomeScreen
import me.abhigya.bourbon.core.ui.onboarding.OnBoardingScreen
import me.abhigya.bourbon.domain.UserRepository
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

object RouterScreen : AppScreen {

    @Composable
    override operator fun invoke() {
        val currentContext = LocalContext.current
        val coroutine = rememberCoroutineScope()
        val viewModel: RouterViewModel = remember(coroutine) { get { parametersOf(coroutine) } }
        val routerState: Backstack<RoutePath> by viewModel.observeStates().collectAsState()

        CompositionLocalProvider(LocalRouter provides viewModel) {
            routerState.renderCurrentDestination(
                route = {
                    when (it) {
                        RoutePath.HOME -> HomeScreen()
                        RoutePath.AUTH -> AuthScreen()
                        RoutePath.ONBOARDING -> OnBoardingScreen()
                    }
                },
                notFound = { }
            )
        }


        LaunchedEffect(coroutine) {
            val user = get<UserRepository> { parametersOf(currentContext) }
            delay(100)
            user.signOut().single()
            if (user.isLoggedIn().single().not()) {
                viewModel.trySend(RouterContract.Inputs.GoToDestination(RoutePath.AUTH.directions().build()))
            }
        }
    }
}

val LocalRouter = staticCompositionLocalOf<RouterViewModel> {
    error("Router not provided")
}
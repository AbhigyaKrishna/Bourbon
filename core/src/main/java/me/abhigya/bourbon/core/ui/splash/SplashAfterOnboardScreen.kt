package me.abhigya.bourbon.core.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.abhigya.bourbon.core.R
import me.abhigya.bourbon.core.ui.router.LocalRouter
import me.abhigya.bourbon.core.ui.router.RoutePath
import me.abhigya.bourbon.domain.UserRepository
import org.koin.core.component.get
import kotlin.system.measureTimeMillis

object SplashAfterOnboardScreen : SplashScreen() {

    @Composable
    override fun Content() {
        val coroutine = rememberCoroutineScope()
        val router = LocalRouter.current
        val userRepository: UserRepository = get()
        LaunchedEffect(coroutine) {
            val time = measureTimeMillis {
                withContext(Dispatchers.IO) {
                    userRepository.loadUserFully()
                }
            }
            delay(2000 - time)
            router.trySend(RouterContract.Inputs.RestoreBackstack(listOf(RoutePath.HOME.directions().build())))
        }

        Text(text = stringResource(R.string.onboard_you_are_all_set))
    }

}
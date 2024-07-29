package me.abhigya.bourbon

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.core.AndroidLogger
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.plusAssign
import me.abhigya.bourbon.core.ui.auth.AuthContract
import me.abhigya.bourbon.core.ui.router.module
import org.koin.dsl.module

val coreModule = module {
    factory {
        BallastViewModelConfiguration.Builder()
            .apply {
                this += LoggingInterceptor()
                logger = { AndroidLogger(it) }
            }
    }
}

val modules = listOf(
    coreModule,
    RouterContract.module,
    AuthContract.module
)
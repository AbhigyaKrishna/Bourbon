package me.abhigya.bourbon

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.core.AndroidLogger
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.plusAssign
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
    coreModule
)
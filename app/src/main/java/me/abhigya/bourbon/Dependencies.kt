package me.abhigya.bourbon

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.core.AndroidLogger
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.plusAssign
import me.abhigya.bourbon.core.ui.auth.AuthContract
import me.abhigya.bourbon.core.ui.caloriecalc.CalorieViewerContract
import me.abhigya.bourbon.core.ui.exercises.ExerciseListContract
import me.abhigya.bourbon.core.ui.home.HomeContract
import me.abhigya.bourbon.core.ui.onboarding.OnBoardingContract
import me.abhigya.bourbon.core.ui.recipe.MakeSomethingOutOfContract
import me.abhigya.bourbon.core.ui.router.module
import me.abhigya.bourbon.data.dataModules
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
    dataModules,
    RouterContract.module,
    HomeContract.module,
    AuthContract.module,
    OnBoardingContract.module,
    ExerciseListContract.module,
    CalorieViewerContract.module,
    MakeSomethingOutOfContract.module
)
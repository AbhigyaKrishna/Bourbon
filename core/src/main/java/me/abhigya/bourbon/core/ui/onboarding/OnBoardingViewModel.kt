package me.abhigya.bourbon.core.ui.onboarding

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope
import me.abhigya.bourbon.domain.entities.AgeGroup
import me.abhigya.bourbon.domain.entities.Centimeters
import me.abhigya.bourbon.domain.entities.Gender
import me.abhigya.bourbon.domain.entities.Kilograms
import org.koin.dsl.module

class OnBoardingViewModel(
    coroutineScope: CoroutineScope,
    config: BallastViewModelConfiguration<OnBoardingContract.Inputs, OnBoardingContract.Events, OnBoardingContract.State>
) : AndroidViewModel<OnBoardingContract.Inputs, OnBoardingContract.Events, OnBoardingContract.State>(config, coroutineScope)

object OnBoardingContract {

    data class State(
        val weight: Kilograms = Kilograms(),
        val height: Centimeters = Centimeters(),
        val gender: Gender = Gender.Male,
        val ageGroup: AgeGroup = AgeGroup._18_29
    )

    sealed interface Inputs {
        data class WeightChanged(val weight: Kilograms) : Inputs
        data class HeightChanged(val height: Centimeters) : Inputs
        data class GenderChanged(val gender: Gender) : Inputs
        data class AgeGroupChanged(val ageGroup: AgeGroup): Inputs
        data object NextButton : Inputs
    }

    sealed interface Events

    val module = module {
        factory {
            OnBoardingInputHandler()
        }

        factory { (coroutineScope: CoroutineScope) ->
            OnBoardingViewModel(
                coroutineScope,
                get<BallastViewModelConfiguration.Builder>()
                    .withViewModel(
                        initialState = State(),
                        inputHandler = get<OnBoardingInputHandler>(),
                        name = "OnBoardingScreen"
                    )
                    .build()
            )
        }
    }

}

class OnBoardingInputHandler : InputHandler<OnBoardingContract.Inputs, OnBoardingContract.Events, OnBoardingContract.State> {
    override suspend fun InputHandlerScope<OnBoardingContract.Inputs, OnBoardingContract.Events, OnBoardingContract.State>.handleInput(
        input: OnBoardingContract.Inputs
    ) {
        when (input) {
            is OnBoardingContract.Inputs.WeightChanged -> updateState { it.copy(weight = input.weight) }
            is OnBoardingContract.Inputs.HeightChanged -> updateState { it.copy(height = input.height) }
            is OnBoardingContract.Inputs.GenderChanged -> updateState { it.copy(gender = input.gender) }
            is OnBoardingContract.Inputs.AgeGroupChanged -> updateState { it.copy(ageGroup = input.ageGroup) }
            is OnBoardingContract.Inputs.NextButton -> { }
        }
    }
}
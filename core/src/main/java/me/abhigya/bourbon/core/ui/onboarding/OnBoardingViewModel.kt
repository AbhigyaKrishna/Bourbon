package me.abhigya.bourbon.core.ui.onboarding

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope
import me.abhigya.bourbon.domain.entities.Gender
import org.koin.dsl.module

class OnBoardingViewModel(
    coroutineScope: CoroutineScope,
    config: BallastViewModelConfiguration<OnBoardingContract.Inputs, OnBoardingContract.Events, OnBoardingContract.State>
) : AndroidViewModel<OnBoardingContract.Inputs, OnBoardingContract.Events, OnBoardingContract.State>(config, coroutineScope)

object OnBoardingContract {

    enum class Step {
        Weight,
        Height,
        GenderAndAge,
        BmiScale,
        GoalAndAim,
        Training,
        ActivityLevel,
        Diet,
        MealFrequency,
        ;
    }

    data class State(
        val step: Step = Step.Weight,
        val weight: Weight = Weight(),
        val height: Height = Height(),
        val gender: Gender = Gender.Male,
        val age: Int = 0
    )

    enum class WeightUnit(private val display: String) {
        Kilograms("kg"),
        Pounds("lbs"),
        ;

        override fun toString(): String = display
    }

    enum class HeightUnit(private val display: String) {
        Centimeters("cm"),
        Inches("inches"),
        ;

        override fun toString(): String = display
    }

    data class Weight(val value: Int = 0, val unit: WeightUnit = WeightUnit.Kilograms)

    data class Height(val value: Int = 0, val unit: HeightUnit = HeightUnit.Centimeters)

    sealed interface Inputs {
        data class ChangeStep(val step: Step) : Inputs
        data class WeightChanged(val weight: Weight) : Inputs
        data class HeightChanged(val height: Height) : Inputs
        data class GenderChanged(val gender: Gender) : Inputs
        data class AgeChanged(val age: Int): Inputs
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
            is OnBoardingContract.Inputs.ChangeStep -> updateState { it.copy(step = input.step) }
            is OnBoardingContract.Inputs.WeightChanged -> updateState { it.copy(weight = input.weight) }
            is OnBoardingContract.Inputs.HeightChanged -> updateState { it.copy(height = input.height) }
            is OnBoardingContract.Inputs.GenderChanged -> updateState { it.copy(gender = input.gender) }
            is OnBoardingContract.Inputs.AgeChanged -> updateState { it.copy(age = input.age) }
            is OnBoardingContract.Inputs.NextButton -> {
                val nextStep = getCurrentState().step.next
                if (nextStep != null) {
                    updateState { it.copy(step = nextStep) }
                } else {
                    // TODO
                }
            }
        }
    }
}

val OnBoardingContract.Step.next get() = OnBoardingContract.Step.entries.getOrNull(ordinal + 1)
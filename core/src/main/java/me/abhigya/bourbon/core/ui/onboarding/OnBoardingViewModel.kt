package me.abhigya.bourbon.core.ui.onboarding

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope
import me.abhigya.bourbon.domain.entities.Centimeters
import me.abhigya.bourbon.domain.entities.Gender
import me.abhigya.bourbon.domain.entities.Kilograms
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
    enum class Goals {
        WeightLoss,
        WeightGain,
        GainMuscles,
        ;
    }

    enum class DefaultTraining {
        Arms,
        Chest,
        Shoulder,
        Core,
        Legs
    }

    data class State(
        val step: Step = Step.Weight,
        val weight: Kilograms = Kilograms(40),
        val height: Centimeters = Centimeters(100),
        val gender: Gender = Gender.Male,
        val age: Int = 0,
        val goal: Goals = Goals.WeightLoss,
        val aimWeight: Kilograms = Kilograms(0),
        val training: Set<DefaultTraining> = mutableSetOf(),
    )

    sealed interface Inputs {
        data class ChangeStep(val step: Step) : Inputs
        data class WeightChanged(val weight: Kilograms) : Inputs
        data class HeightChanged(val height: Centimeters) : Inputs
        data class GenderChanged(val gender: Gender) : Inputs
        data class AgeChanged(val age: Int) : Inputs
        data class GoalChanged(val goal: Goals) : Inputs
        data class AimWeightChanged(val weight: Kilograms) : Inputs
        data class TrainingChanged(val training: AddRemove) : Inputs {
            sealed interface AddRemove
            data class Add(val training: DefaultTraining) : AddRemove
            data class Remove(val training: DefaultTraining) : AddRemove
        }
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
            is OnBoardingContract.Inputs.GoalChanged -> updateState { it.copy(goal = input.goal) }
            is OnBoardingContract.Inputs.AimWeightChanged -> updateState { it.copy(aimWeight = input.weight) }
            is OnBoardingContract.Inputs.TrainingChanged -> {
                when (val training = input.training) {
                    is OnBoardingContract.Inputs.TrainingChanged.Add -> updateState { it.copy(training = it.training + training.training) }
                    is OnBoardingContract.Inputs.TrainingChanged.Remove -> updateState { it.copy(training = it.training - training.training) }
                }
            }
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
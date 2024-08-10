package me.abhigya.bourbon.core.ui.onboarding

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext
import me.abhigya.bourbon.core.ui.AddRemove
import me.abhigya.bourbon.core.ui.router.RoutePath
import me.abhigya.bourbon.core.ui.router.RouterViewModel
import me.abhigya.bourbon.domain.UserRepository
import me.abhigya.bourbon.domain.entities.ActivityLevel
import me.abhigya.bourbon.domain.entities.Centimeters
import me.abhigya.bourbon.domain.entities.Days
import me.abhigya.bourbon.domain.entities.DefaultTraining
import me.abhigya.bourbon.domain.entities.DietGuide
import me.abhigya.bourbon.domain.entities.DietPreference
import me.abhigya.bourbon.domain.entities.Gender
import me.abhigya.bourbon.domain.entities.Goal
import me.abhigya.bourbon.domain.entities.Kilograms
import me.abhigya.bourbon.domain.entities.UserData
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
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
        val weight: Kilograms = Kilograms(40),
        val height: Centimeters = Centimeters(100),
        val gender: Gender = Gender.Male,
        val age: Int = 0,
        val goal: Goal = Goal.WeightLoss,
        val aimWeight: Kilograms = Kilograms(0),
        val training: Set<DefaultTraining> = mutableSetOf(),
        val workoutDays: Set<Days> = mutableSetOf(),
        val activityLevel: ActivityLevel = ActivityLevel.Sedentary,
        val dietGuide: DietGuide = DietGuide.PreMade,
        val dietPreference: DietPreference = DietPreference.Vegetarian,
        val mealFrequency: Int = 1,
    )

    sealed interface Inputs {
        data class ChangeStep(val step: Step) : Inputs
        data class WeightChanged(val weight: Kilograms) : Inputs
        data class HeightChanged(val height: Centimeters) : Inputs
        data class GenderChanged(val gender: Gender) : Inputs
        data class AgeChanged(val age: Int) : Inputs
        data class GoalChanged(val goal: Goal) : Inputs
        data class AimWeightChanged(val weight: Kilograms) : Inputs
        data class TrainingChanged(val training: AddRemove<DefaultTraining>) : Inputs
        data class WorkoutDaysChanged(val days: AddRemove<Days>) : Inputs
        data class ActivityLevelChanged(val activityLevel: ActivityLevel) : Inputs
        data class DietGuideChanged(val dietGuide: DietGuide) : Inputs
        data class DietPreferenceChanged(val dietPreference: DietPreference) : Inputs
        data class MealFrequencyChanged(val frequency: Int) : Inputs
        data object NextButton : Inputs
    }

    sealed interface Events

    val module = module {
        factory { (router: RouterViewModel) ->
            OnBoardingInputHandler(router, get())
        }

        viewModel { (coroutineScope: CoroutineScope, router: RouterViewModel) ->
            OnBoardingViewModel(
                coroutineScope,
                get<BallastViewModelConfiguration.Builder>()
                    .withViewModel(
                        initialState = State(),
                        inputHandler = get<OnBoardingInputHandler> { parametersOf(router) },
                        name = "OnBoardingScreen"
                    )
                    .build()
            )
        }
    }

}

class OnBoardingInputHandler(
    private val router: RouterViewModel,
    private val userRepository: UserRepository
) : InputHandler<OnBoardingContract.Inputs, OnBoardingContract.Events, OnBoardingContract.State> {
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
                    is AddRemove.Add -> updateState { it.copy(training = it.training + training.item) }
                    is AddRemove.Remove -> updateState { it.copy(training = it.training - training.item) }
                }
            }
            is OnBoardingContract.Inputs.WorkoutDaysChanged -> {
                when (val days = input.days) {
                    is AddRemove.Add -> updateState { it.copy(workoutDays = it.workoutDays + days.item) }
                    is AddRemove.Remove -> updateState { it.copy(workoutDays = it.workoutDays - days.item) }
                }
            }
            is OnBoardingContract.Inputs.ActivityLevelChanged -> updateState { it.copy(activityLevel = input.activityLevel) }
            is OnBoardingContract.Inputs.DietGuideChanged -> updateState { it.copy(dietGuide = input.dietGuide) }
            is OnBoardingContract.Inputs.DietPreferenceChanged -> updateState { it.copy(dietPreference = input.dietPreference) }
            is OnBoardingContract.Inputs.MealFrequencyChanged -> updateState { it.copy(mealFrequency = input.frequency.coerceIn(1..8)) }
            is OnBoardingContract.Inputs.NextButton -> {
                val nextStep = getCurrentState().step.next
                if (nextStep != null) {
                    updateState { it.copy(step = nextStep) }
                } else {
                    val userData = getCurrentState().toUserData()
                    sideJob("save-date") {
                        withContext(Dispatchers.IO) {
                            userRepository.saveData(userRepository.currentUser().single().copy(data = userData)).launchIn(this).join()
                        }
                    }
                    router.trySend(RouterContract.Inputs.GoToDestination(RoutePath.SPLASH_AFTER_ONBOARDING.directions().build()))
                }
            }
        }
    }
}

val OnBoardingContract.Step.next get() = OnBoardingContract.Step.entries.getOrNull(ordinal + 1)

private fun OnBoardingContract.State.toUserData(): UserData = UserData(
    weight = weight,
    height = height,
    gender = gender,
    age = age,
    goal = goal,
    aimWeight = aimWeight,
    training = training,
    workoutDays = workoutDays,
    activityLevel = activityLevel,
    dietGuide = dietGuide,
    dietPreference = dietPreference,
    mealFrequency = mealFrequency
)
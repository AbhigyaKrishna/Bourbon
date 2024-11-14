package me.abhigya.bourbon.core.ui.exercises

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.abhigya.bourbon.domain.ExerciseRepository
import me.abhigya.bourbon.domain.entities.Exercise
import me.abhigya.bourbon.domain.entities.ExerciseData
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class ExerciseListViewModel(
    coroutine: CoroutineScope,
    config: BallastViewModelConfiguration<ExerciseListContract.Inputs, ExerciseListContract.Events, ExerciseListContract.State>,
    exerciseRepository: ExerciseRepository,
    exercises: List<Exercise>
) : AndroidViewModel<ExerciseListContract.Inputs, ExerciseListContract.Events, ExerciseListContract.State>(config, coroutine) {

    private val _exerciseData: MutableStateFlow<Map<String, ExerciseData>> = MutableStateFlow(emptyMap())
    val exerciseData: StateFlow<Map<String, ExerciseData>> = _exerciseData.asStateFlow()
    init {
        coroutine.launch {
            val map = HashMap<String, ExerciseData>()
            exerciseRepository.getExerciseData(exercises.map { it.id })
                .collect {
                    it.onSuccess { value ->
                        map[value.id] = value
                    }
                }
            _exerciseData.value = map
        }
    }

}

object ExerciseListContract {

    data class State(
        val shownIndex: Int = 0,
        val exercises: List<Exercise> = emptyList()
    )

    sealed interface Inputs {
        data object Next : Inputs
        data object Previous : Inputs
        data class JumpTo(val index: Int) : Inputs
    }

    sealed interface Events

    val module = module {
        viewModel { (coroutineScope: CoroutineScope, state: State, exercises: List<Exercise>) ->
            ExerciseListViewModel(
                coroutineScope,
                get<BallastViewModelConfiguration.Builder>()
                    .withViewModel(
                        initialState = state,
                        inputHandler = ExerciseListInputHandler,
                        name = "ExerciseListViewModel"
                    )
                    .build(),
                get(),
                exercises
            )

        }
    }

}

object ExerciseListInputHandler : InputHandler<ExerciseListContract.Inputs, ExerciseListContract.Events, ExerciseListContract.State> {

    override suspend fun InputHandlerScope<ExerciseListContract.Inputs, ExerciseListContract.Events, ExerciseListContract.State>.handleInput(
        input: ExerciseListContract.Inputs
    ) {
        val currentState = getCurrentState()
        when (input) {
            is ExerciseListContract.Inputs.Next -> {
                if (currentState.shownIndex < currentState.exercises.size) {
                    updateState { it.copy(shownIndex = it.shownIndex + 1) }
                }
            }
            is ExerciseListContract.Inputs.Previous -> {
                if (currentState.shownIndex > 0) {
                    updateState { it.copy(shownIndex = it.shownIndex - 1) }
                }
            }
            is ExerciseListContract.Inputs.JumpTo -> {
                if (input.index in currentState.exercises.indices) {
                    updateState { it.copy(shownIndex = input.index) }
                }
            }
        }
    }

}
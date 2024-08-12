package me.abhigya.bourbon.core.ui.exercises

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import co.touchlab.stately.collections.ConcurrentMutableMap
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import me.abhigya.bourbon.domain.ExerciseRepository
import me.abhigya.bourbon.domain.entities.Exercise
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class ExerciseListViewModel(
    coroutine: CoroutineScope,
    exerciseRepository: ExerciseRepository,
    config: BallastViewModelConfiguration<ExerciseListContract.Inputs, ExerciseListContract.Events, ExerciseListContract.State>
) : AndroidViewModel<ExerciseListContract.Inputs, ExerciseListContract.Events, ExerciseListContract.State>(config, coroutine) {

    private val videoCache: MutableMap<Int, Bitmap> = ConcurrentMutableMap()

    init {
        val exercises = observeStates()
        viewModelScope.launch {
//            for ((idx, exercise) in exercises.first().exercises.withIndex()) {
//                val videoUri = exercise.videoUri ?: continue
//                launch(Dispatchers.IO) {
//                    val video = exerciseRepository.getExerciseImageById(videoUri).single().getOrNull() ?: return@launch
//                    videoCache[idx] = video
//                    sendAndAwaitCompletion(ExerciseListContract.Inputs.RaiseEvent(ExerciseListContract.Events.VideoLoaded(idx, video)))
//                }
//            }
        }
    }

    fun getVideo(index: Int): Bitmap? {
        return videoCache[index]
    }

    override fun onCleared() {
        videoCache.clear()
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
        data class RaiseEvent(val event: Events) : Inputs
    }

    sealed interface Events {
        data class VideoLoaded(val index: Int, val video: Bitmap) : Events
    }

    val module = module {
        viewModel { (coroutineScope: CoroutineScope, state: State) ->
            ExerciseListViewModel(
                coroutineScope,
                get(),
                get<BallastViewModelConfiguration.Builder>()
                    .withViewModel(
                        initialState = state,
                        inputHandler = ExerciseListInputHandler,
                        name = "ExerciseListViewModel"
                    )
                    .build()
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
            is ExerciseListContract.Inputs.RaiseEvent -> postEvent(input.event)
        }
    }

}
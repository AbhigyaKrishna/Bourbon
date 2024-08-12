package me.abhigya.bourbon.core.ui.recipe

import android.graphics.Bitmap
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope
import me.abhigya.bourbon.core.ui.components.OutputState
import me.abhigya.bourbon.domain.GeminiRepository
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MakeSomethingOutOfViewModel(
    coroutineScope: CoroutineScope,
    config: BallastViewModelConfiguration<MakeSomethingOutOfContract.Inputs, MakeSomethingOutOfContract.Events, MakeSomethingOutOfContract.State>
) : AndroidViewModel<MakeSomethingOutOfContract.Inputs, MakeSomethingOutOfContract.Events, MakeSomethingOutOfContract.State>(config, coroutineScope)

object MakeSomethingOutOfContract {

    data class State(
        val image: Bitmap? = null,
        val outputState: OutputState = OutputState.None
    )

    sealed interface Inputs {
        data class ImageChanged(val image: Bitmap) : Inputs
        data class OutputStateChanged(val outputState: OutputState) : Inputs
        data object Find : Inputs
    }

    sealed interface Events;

    val module = module {
        factory {
            MakeSomethingOutOfInputHandler(get())
        }

        viewModel { (coroutine: CoroutineScope) ->
            MakeSomethingOutOfViewModel(
                coroutineScope = coroutine,
                config = get<BallastViewModelConfiguration.Builder>()
                    .withViewModel(
                        initialState = State(),
                        inputHandler = get<MakeSomethingOutOfInputHandler>(),
                        name = "MakeSomethingOutOfViewModel"
                    )
                    .build()
            )
        }
    }
}

class MakeSomethingOutOfInputHandler(
    private val geminiRepository: GeminiRepository
) : InputHandler<MakeSomethingOutOfContract.Inputs, MakeSomethingOutOfContract.Events, MakeSomethingOutOfContract.State> {

    override suspend fun InputHandlerScope<MakeSomethingOutOfContract.Inputs, MakeSomethingOutOfContract.Events, MakeSomethingOutOfContract.State>.handleInput(
        input: MakeSomethingOutOfContract.Inputs
    ) {
        when (input) {
            is MakeSomethingOutOfContract.Inputs.ImageChanged -> updateState { it.copy(image = input.image) }
            is MakeSomethingOutOfContract.Inputs.OutputStateChanged -> updateState { it.copy(outputState = input.outputState) }
            is MakeSomethingOutOfContract.Inputs.Find -> {
                val state = getCurrentState()
                state.image ?: return
                if (state.outputState == OutputState.Loading) return
                updateState { it.copy(outputState = OutputState.Loading) }

                sideJob("gemini-calorie") {
                    geminiRepository.promptRecipeFromItem(state.image)
                        .collect { fetch ->
                            postInput(
                                MakeSomethingOutOfContract.Inputs.OutputStateChanged(
                                    fetch.fold(
                                        { OutputState.Data(it) },
                                        { OutputState.Error(it) }
                                    )
                                )
                            )
                        }
                }
            }
        }
    }

}
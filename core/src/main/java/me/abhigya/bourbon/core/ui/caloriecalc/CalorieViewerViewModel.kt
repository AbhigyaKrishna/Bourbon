package me.abhigya.bourbon.core.ui.caloriecalc

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
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

class CalorieViewerViewModel(
    coroutineScope: CoroutineScope,
    config: BallastViewModelConfiguration<CalorieViewerContract.Inputs, CalorieViewerContract.Events, CalorieViewerContract.State>
) : AndroidViewModel<CalorieViewerContract.Inputs, CalorieViewerContract.Events, CalorieViewerContract.State>(config, coroutineScope) {
    init {
        attachEventHandler(coroutineScope, CalorieViewerEventHandler)
    }
}

object CalorieViewerContract {

    enum class QuantityUnit {
        Grams,
        Kilograms,
        Pounds
    }

    data class State(
        val item: String = "",
        val amount: Float = 0f,
        val unit: QuantityUnit = QuantityUnit.Grams,
        val outputState: OutputState = OutputState.None
    )

    sealed interface Inputs {
        data class ItemChanged(val item: String) : Inputs
        data class AmountChanged(val amount: Float) : Inputs
        data class UnitChanged(val unit: QuantityUnit) : Inputs
        data class OutPutStateChanged(val outputState: OutputState) : Inputs
        data object Fetch : Inputs
    }

    sealed interface Events {
        data class Fetched(val text: String) : Events
        data class Error(val error: Throwable) : Events
    }

    val module = module {
        viewModel { (coroutineScope: CoroutineScope) ->
            CalorieViewerViewModel(
                coroutineScope,
                get<BallastViewModelConfiguration.Builder>()
                    .withViewModel(
                        initialState = State(),
                        inputHandler = CalorieViewerInputHandler(get()),
                        name = "CalorieViewerViewModel"
                    )
                    .build()
            )
        }
    }

}

class CalorieViewerInputHandler(
    private val geminiRepository: GeminiRepository
) : InputHandler<CalorieViewerContract.Inputs, CalorieViewerContract.Events, CalorieViewerContract.State> {

    override suspend fun InputHandlerScope<CalorieViewerContract.Inputs, CalorieViewerContract.Events, CalorieViewerContract.State>.handleInput(
        input: CalorieViewerContract.Inputs
    ) {
        when (input) {
            is CalorieViewerContract.Inputs.ItemChanged -> updateState { it.copy(item = input.item) }
            is CalorieViewerContract.Inputs.AmountChanged -> updateState { it.copy(amount = input.amount) }
            is CalorieViewerContract.Inputs.UnitChanged -> updateState { it.copy(unit = input.unit) }
            is CalorieViewerContract.Inputs.OutPutStateChanged -> updateState { it.copy(outputState = input.outputState) }
            is CalorieViewerContract.Inputs.Fetch -> {
                val state = getCurrentState()
                if (state.outputState == OutputState.Loading) return
                updateState { it.copy(outputState = OutputState.Loading) }
                sideJob("gemini-calorie") {
                    geminiRepository.promptCalorieFetch(state.item, state.amount, state.unit.name)
                        .collect { fetch ->
                            postEvent(
                                fetch.fold(
                                    { CalorieViewerContract.Events.Fetched(it) },
                                    { CalorieViewerContract.Events.Error(it) }
                                )
                            )
                        }
                }
            }
        }
    }

}

object CalorieViewerEventHandler : EventHandler<CalorieViewerContract.Inputs, CalorieViewerContract.Events, CalorieViewerContract.State> {

    override suspend fun EventHandlerScope<CalorieViewerContract.Inputs, CalorieViewerContract.Events, CalorieViewerContract.State>.handleEvent(
        event: CalorieViewerContract.Events
    ) {
        when (event) {
            is CalorieViewerContract.Events.Fetched -> postInput(CalorieViewerContract.Inputs.OutPutStateChanged(OutputState.Data(event.text)))
            is CalorieViewerContract.Events.Error -> postInput(CalorieViewerContract.Inputs.OutPutStateChanged(OutputState.Error(event.error)))
        }
    }

}
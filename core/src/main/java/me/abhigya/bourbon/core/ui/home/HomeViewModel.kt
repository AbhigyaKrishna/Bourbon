package me.abhigya.bourbon.core.ui.home

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import me.abhigya.bourbon.domain.UserRepository
import me.abhigya.bourbon.domain.entities.User
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class HomeViewModel(
    coroutine: CoroutineScope,
    config: BallastViewModelConfiguration<HomeContract.Inputs, HomeContract.Events, HomeContract.State>,
    private val userRepository: UserRepository
) : AndroidViewModel<HomeContract.Inputs, HomeContract.Events, HomeContract.State>(config, coroutine) {

    fun fetchUser(): Flow<User> {
        return userRepository.currentUser()
    }

}

object HomeContract {

    data class State(
        val date: LocalDate = java.time.LocalDate.now().toKotlinLocalDate(),
        val selectedDate: LocalDate = date
    )

    sealed interface Inputs {
        data class SelectDate(val date: LocalDate) : Inputs
    }

    sealed interface Events

    val module = module {
        viewModel { (coroutine: CoroutineScope) ->
            HomeViewModel(
                coroutine,
                get<BallastViewModelConfiguration.Builder>()
                    .withViewModel(
                        initialState = State(),
                        inputHandler = HomeInputHandler,
                        name = "HomeScreen"
                    )
                    .build(),
                get()
            )
        }
    }

}

object HomeInputHandler : InputHandler<HomeContract.Inputs, HomeContract.Events, HomeContract.State> {

    override suspend fun InputHandlerScope<HomeContract.Inputs, HomeContract.Events, HomeContract.State>.handleInput(
        input: HomeContract.Inputs
    ) {
        when (input) {
            is HomeContract.Inputs.SelectDate -> updateState { it.copy(selectedDate = input.date) }
        }
    }

}
package me.abhigya.bourbon.core.ui.auth

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.single
import me.abhigya.bourbon.core.ui.router.RoutePath
import me.abhigya.bourbon.core.ui.router.RouterViewModel
import me.abhigya.bourbon.domain.UserRepository
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

class AuthViewModel(
    coroutineScope: CoroutineScope,
    config: BallastViewModelConfiguration<AuthContract.Inputs, AuthContract.Events, AuthContract.State>,
    eventsHandler: AuthEventsHandler
) : AndroidViewModel<AuthContract.Inputs, AuthContract.Events, AuthContract.State>(config, coroutineScope) {
    init {
        attachEventHandler(handler = eventsHandler)
    }
}

object AuthContract {

    enum class AuthType {
        LOGIN,
        REGISTER,
        ;
    }

    data class State(
        val isLoading: Boolean = false,
        val authType: AuthType,
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val passwordVisualState: VisualTransformation = PasswordVisualTransformation()
    )

    sealed interface Inputs {
        data class ChangeLoadingState(val state: Boolean) : Inputs
        data class EmailChanged(val email: String) : Inputs
        data class PasswordChanged(val password: String) : Inputs
        data class ConfirmPasswordChanged(val confirmPassword: String) : Inputs
        data object PasswordVisibilityChanged : Inputs
        data object ConfirmButton : Inputs
        data class SwitchAuthType(val authType: AuthType) : Inputs
        data class SignInByGoogle(val context: Context) : Inputs
    }

    sealed interface Events {
        data class SignInResult(val result: Result<Unit>) : Events
    }

    val module = module {
        factory { (router: RouterViewModel) ->
            AuthInputHandler(router, get())
        }

        factory { (router: RouterViewModel) ->
            AuthEventsHandler(router, get())
        }

        viewModel { (coroutineScope: CoroutineScope, router: RouterViewModel) ->
            AuthViewModel(
                coroutineScope,
                get<BallastViewModelConfiguration.Builder>()
                    .withViewModel(
                        initialState = State(authType = AuthType.LOGIN),
                        inputHandler = get<AuthInputHandler> { parametersOf(router) },
                        name = "AuthScreen"
                    )
                    .build(),
                eventsHandler = get { parametersOf(router) }
            )
        }
    }

}

class AuthInputHandler(
    private val router: RouterViewModel,
    private val userRepository: UserRepository
) : InputHandler<AuthContract.Inputs, AuthContract.Events, AuthContract.State> {

    override suspend fun InputHandlerScope<AuthContract.Inputs, AuthContract.Events, AuthContract.State>.handleInput(
        input: AuthContract.Inputs
    ) {
        when (input) {
            is AuthContract.Inputs.ChangeLoadingState -> updateState { it.copy(isLoading = input.state) }
            is AuthContract.Inputs.EmailChanged -> updateState { it.copy(email = input.email) }
            is AuthContract.Inputs.PasswordChanged -> updateState { it.copy(password = input.password) }
            is AuthContract.Inputs.ConfirmPasswordChanged -> updateState { it.copy(confirmPassword = input.confirmPassword) }
            is AuthContract.Inputs.PasswordVisibilityChanged -> updateState {
                it.copy(
                    passwordVisualState = if (it.passwordVisualState is PasswordVisualTransformation) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    }
                )
            }
            is AuthContract.Inputs.ConfirmButton -> {
                sideJob("onboarding") {
                    router.trySend(RouterContract.Inputs.GoToDestination(RoutePath.ONBOARDING.directions().build()))
                }
                if (getCurrentState().authType == AuthContract.AuthType.LOGIN) {
                    TODO() // login
                } else {
                    TODO() // register
                }
            }
            is AuthContract.Inputs.SwitchAuthType -> updateState { it.copy(authType = input.authType) }
            is AuthContract.Inputs.SignInByGoogle -> {
                updateState { it.copy(isLoading = true) }
                sideJob("googleSignIn") {
                    postEvent(AuthContract.Events.SignInResult(userRepository.signIn().withGoogle(input.context).first()))
                }
            }
        }
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
class AuthEventsHandler(
    private val router: RouterViewModel,
    private val userRepository: UserRepository
) : EventHandler<AuthContract.Inputs, AuthContract.Events, AuthContract.State> {
    override suspend fun EventHandlerScope<AuthContract.Inputs, AuthContract.Events, AuthContract.State>.handleEvent(
        event: AuthContract.Events
    ) {
        when (event) {
            is AuthContract.Events.SignInResult -> {
                postInput(AuthContract.Inputs.ChangeLoadingState(false))
                if (event.result.isSuccess) {
                    val hasData = userRepository.currentUser().flatMapLatest { userRepository.hasData(it) }.single()
                    Log.d("AuthEventsHandler", "hasData: $hasData")
                    router.trySend(RouterContract.Inputs.GoToDestination((if (hasData) RoutePath.HOME else RoutePath.ONBOARDING).directions().build()))
                } else {
                    // show error
                }
            }
        }
    }
}

fun AuthContract.AuthType.inverse(): AuthContract.AuthType {
    return if (this == AuthContract.AuthType.LOGIN) {
        AuthContract.AuthType.REGISTER
    } else {
        AuthContract.AuthType.LOGIN
    }
}

package me.abhigya.bourbon.core.ui.auth

import android.content.Context
import android.widget.Toast
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
import kotlinx.coroutines.flow.single
import me.abhigya.bourbon.core.ui.router.RoutePath
import me.abhigya.bourbon.core.ui.router.RouterViewModel
import me.abhigya.bourbon.domain.UserRepository
import me.abhigya.bourbon.domain.validateEmail
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

    enum class PasswordErrorType(val display: String? = null) {
        NONE,
        WRONG("Wrong password"),
        NOT_MATCH("Passwords do not match"),
        NOT_FOUND("User not found"),
    }

    data class State(
        val isLoading: Boolean = false,
        val authType: AuthType,
        val email: String = "",
        val isWrongEmail: Boolean = false,
        val password: String = "",
        val confirmPassword: String = "",
        val passwordErrorState: PasswordErrorType = PasswordErrorType.NONE,
        val passwordVisualState: VisualTransformation = PasswordVisualTransformation()
    )

    sealed interface Inputs {
        data class ChangeLoadingState(val state: Boolean) : Inputs
        data class EmailChanged(val email: String) : Inputs
        data class WrongEmailChanged(val state: Boolean): Inputs
        data class PasswordChanged(val password: String) : Inputs
        data class ConfirmPasswordChanged(val confirmPassword: String) : Inputs
        data object PasswordVisibilityChanged : Inputs
        data class PasswordErrorChanged(val error: PasswordErrorType) : Inputs
        data object ConfirmButton : Inputs
        data class SwitchAuthType(val authType: AuthType) : Inputs
        data class SignInByGoogle(val context: Context) : Inputs
    }

    sealed interface Events {
        data class SignInResult(val result: Result<Unit>) : Events
    }

    val module = module {
        factory {
            AuthInputHandler(get())
        }

        factory { (router: RouterViewModel, context: Context) ->
            AuthEventsHandler(context, router, get())
        }

        viewModel { (coroutineScope: CoroutineScope, router: RouterViewModel, context: Context) ->
            AuthViewModel(
                coroutineScope,
                get<BallastViewModelConfiguration.Builder>()
                    .withViewModel(
                        initialState = State(authType = AuthType.LOGIN),
                        inputHandler = get<AuthInputHandler>(),
                        name = "AuthScreen"
                    )
                    .build(),
                eventsHandler = get { parametersOf(router, context) }
            )
        }
    }

}

class AuthInputHandler(
    private val userRepository: UserRepository
) : InputHandler<AuthContract.Inputs, AuthContract.Events, AuthContract.State> {

    override suspend fun InputHandlerScope<AuthContract.Inputs, AuthContract.Events, AuthContract.State>.handleInput(
        input: AuthContract.Inputs
    ) {
        when (input) {
            is AuthContract.Inputs.ChangeLoadingState -> updateState { it.copy(isLoading = input.state) }
            is AuthContract.Inputs.EmailChanged -> updateState { it.copy(email = input.email) }
            is AuthContract.Inputs.WrongEmailChanged -> updateState { it.copy(isWrongEmail = input.state) }
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
            is AuthContract.Inputs.PasswordErrorChanged -> updateState { it.copy(passwordErrorState = input.error) }
            is AuthContract.Inputs.ConfirmButton -> {
                val state = this@handleInput.getCurrentState()
                if (!validateEmail(state.email)) {
                    updateState { it.copy(isWrongEmail = true) }
                    return
                }
                if (state.authType == AuthContract.AuthType.REGISTER) {
                    if (state.password != state.confirmPassword) {
                        updateState { it.copy(passwordErrorState = AuthContract.PasswordErrorType.NOT_MATCH) }
                    } else {
                        sideJob("register") {
                            postEvent(AuthContract.Events.SignInResult(userRepository.signUp().withEmailAndPassword(state.email, state.password).single()))
                        }
                    }
                } else {
                    updateState { it.copy(passwordErrorState = AuthContract.PasswordErrorType.NONE) }
                    sideJob("login") {
                        userRepository.exists(state.email).collect {
                            if (!it) {
                                postInput(AuthContract.Inputs.PasswordErrorChanged(AuthContract.PasswordErrorType.NOT_FOUND))
                                postInput(AuthContract.Inputs.WrongEmailChanged(true))
                                postInput(AuthContract.Inputs.ChangeLoadingState(false))
                                return@collect
                            }
                        }
                        userRepository.signIn().withEmailAndPassword(state.email, state.password).collect {
                            it.onSuccess {
                                postEvent(AuthContract.Events.SignInResult(Result.success(it)))
                            }.onFailure {
                                postInput(AuthContract.Inputs.PasswordErrorChanged(AuthContract.PasswordErrorType.WRONG))
                            }
                        }
                        postInput(AuthContract.Inputs.ChangeLoadingState(false))
                    }
                }
            }
            is AuthContract.Inputs.SwitchAuthType -> updateState { it.copy(authType = input.authType, passwordErrorState = AuthContract.PasswordErrorType.NONE, isWrongEmail = false) }
            is AuthContract.Inputs.SignInByGoogle -> {
                updateState { it.copy(isLoading = true) }
                sideJob("googleSignIn") {
                    postEvent(AuthContract.Events.SignInResult(userRepository.signIn().withGoogle(input.context).single()))
                }
            }
        }
    }

}

class AuthEventsHandler(
    private val context: Context,
    private val router: RouterViewModel,
    private val userRepository: UserRepository
) : EventHandler<AuthContract.Inputs, AuthContract.Events, AuthContract.State> {
    override suspend fun EventHandlerScope<AuthContract.Inputs, AuthContract.Events, AuthContract.State>.handleEvent(
        event: AuthContract.Events
    ) {
        when (event) {
            is AuthContract.Events.SignInResult -> {
                event.result.onSuccess {
                    handleLogin()
                }.onFailure {
                    handleError()
                }
            }
        }
    }

    private suspend fun EventHandlerScope<AuthContract.Inputs, AuthContract.Events, AuthContract.State>.handleLogin() {
        val user = userRepository.currentUser().single()
        val hasData = userRepository.hasData(user).single()
        if (hasData) {
            userRepository.loadUserData(user)
        }
        postInput(AuthContract.Inputs.ChangeLoadingState(false))
        router.trySend(RouterContract.Inputs.GoToDestination((if (hasData) RoutePath.HOME else RoutePath.ONBOARDING).directions().build()))
    }

    private suspend fun EventHandlerScope<AuthContract.Inputs, AuthContract.Events, AuthContract.State>.handleError() {
        postInput(AuthContract.Inputs.ChangeLoadingState(false))
        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
    }
}

fun AuthContract.AuthType.inverse(): AuthContract.AuthType {
    return if (this == AuthContract.AuthType.LOGIN) {
        AuthContract.AuthType.REGISTER
    } else {
        AuthContract.AuthType.LOGIN
    }
}

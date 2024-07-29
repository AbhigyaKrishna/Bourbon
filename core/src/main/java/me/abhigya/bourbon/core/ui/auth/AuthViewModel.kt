package me.abhigya.bourbon.core.ui.auth

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.AndroidViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class AuthViewModel(
    coroutineScope: CoroutineScope,
    config: BallastViewModelConfiguration<AuthContract.Inputs, AuthContract.Events, AuthContract.State>
) : AndroidViewModel<AuthContract.Inputs, AuthContract.Events, AuthContract.State>(config, coroutineScope)

object AuthContract {

    enum class AuthType {
        LOGIN,
        REGISTER,
        ;
    }

    data class State(
        val authType: AuthType,
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val passwordVisualState: VisualTransformation = PasswordVisualTransformation()
    )

    sealed interface Inputs {
        data class EmailChanged(val email: String) : Inputs
        data class PasswordChanged(val password: String) : Inputs
        data class ConfirmPasswordChanged(val confirmPassword: String) : Inputs
        data object PasswordVisibilityChanged : Inputs
        data object ConfirmButton : Inputs
        data class SwitchAuthType(val authType: AuthType) : Inputs
    }

    sealed interface Events {
    }

    val module = module {
        factory {
            AuthInputHandler()
        }

        viewModel { (coroutineScope: CoroutineScope) ->
            AuthViewModel(
                coroutineScope,
                get<BallastViewModelConfiguration.Builder>()
                    .withViewModel(
                        initialState = State(authType = AuthType.LOGIN),
                        inputHandler = get<AuthInputHandler>(),
                        name = "AuthScreen"
                    )
                    .build()
            )
        }
    }

}

class AuthInputHandler : InputHandler<AuthContract.Inputs, AuthContract.Events, AuthContract.State> {

    override suspend fun InputHandlerScope<AuthContract.Inputs, AuthContract.Events, AuthContract.State>.handleInput(
        input: AuthContract.Inputs
    ) {
        when (input) {
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
                if (getCurrentState().authType == AuthContract.AuthType.LOGIN) {
                    TODO() // login
                } else {
                    TODO() // register
                }
            }
            is AuthContract.Inputs.SwitchAuthType -> updateState { it.copy(authType = input.authType) }
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

package me.abhigya.bourbon.core.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.abhigya.bourbon.core.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

object AuthScreen : KoinComponent {

    @Composable
    operator fun invoke() {
        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(8.dp)
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 40.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val viewModelScope = rememberCoroutineScope()
                val viewModel: AuthViewModel = remember(viewModelScope) { get { parametersOf(viewModelScope) } }
                val uiState by viewModel.observeStates().collectAsState()

                Logo()
                Separator()

                var emailText by remember { mutableStateOf("") }
                InputField(
                    label = "Email",
                    placeholder = "Enter Email",
                    value = emailText
                ) {
                    emailText = it
                    viewModel.trySend(AuthContract.Inputs.EmailChanged(it))
                }

                Separator()

                var passwordText by remember { mutableStateOf("") }
                InputField(
                    label = "Password",
                    placeholder = "Enter Password",
                    value = passwordText,
                    errorSuperScript = "Wrong Password",
                    visualTransformation = uiState.passwordVisualState,
                    trailingIcon = {
                        Icon(
                            modifier = Modifier
                                .size(width = 18.dp, height = 12.dp)
                                .clickable {
                                    viewModel.trySend(AuthContract.Inputs.PasswordVisibilityChanged)
                                },
                            painter = painterResource(id = R.drawable.ic_eye),
                            contentDescription = "Show Password",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    passwordText = it
                    viewModel.trySend(AuthContract.Inputs.PasswordChanged(it))
                }

                AnimatedVisibility(visible = uiState.authType == AuthContract.AuthType.REGISTER) {
                    Column {
                        var confirmPasswordText by remember { mutableStateOf("") }
                        viewModel.trySend(AuthContract.Inputs.ConfirmPasswordChanged(confirmPasswordText))

                        Separator(thickness = 16.dp)
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(24.dp),
                            value = confirmPasswordText,
                            onValueChange = {
                                confirmPasswordText = it
                                viewModel.trySend(AuthContract.Inputs.ConfirmPasswordChanged(it))
                            },
                            placeholder = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = "Re-Enter Password",
                                        fontSize = 14.sp
                                    )
                                }
                            },
                            visualTransformation = uiState.passwordVisualState,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                                focusedContainerColor = MaterialTheme.colorScheme.secondary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }

                Separator()
                UIButton(
                    text = if (uiState.authType == AuthContract.AuthType.LOGIN) "Log In" else "Sign Up",
                    primary = true
                ) {
                    viewModel.trySend(AuthContract.Inputs.ConfirmButton)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "or",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
                UIButton(text = if (uiState.authType != AuthContract.AuthType.LOGIN) "Log In" else "Sign Up") {
                    viewModel.trySend(AuthContract.Inputs.SwitchAuthType(uiState.authType.inverse()))
                }
                Separator(thickness = 16.dp)
                UIButton(
                    text = "Sign in with Google",
                    contentColor = Color.White,
                    logo = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google"
                        )
                    }
                )

                ForgotPassword() {
                    // TODO
                }
            }
        }
    }

    @Composable
    internal fun Logo(modifier: Modifier = Modifier) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = modifier
        )
    }

    @Composable
    internal fun Separator(modifier: Modifier = Modifier, thickness: Dp = 24.dp) {
        HorizontalDivider(
            thickness = thickness,
            modifier = modifier
        )
    }

    @Composable
    internal fun InputField(
        modifier: Modifier = Modifier,
        label: String,
        placeholder: String = label,
        value: String = "",
        errorSuperScript: String? = null,
        fontSize: TextUnit = 11.sp,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        trailingIcon: (@Composable () -> Unit)? = null,
        onValueChange: (String) -> Unit = {}
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(23.dp),
            contentAlignment = Alignment.TopStart
        ) {
            if (errorSuperScript != null) {
                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = modifier,
                        text = label,
                        fontSize = fontSize
                    )
                    // TODO Check error
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier.size(11.dp),
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        VerticalDivider(thickness = 4.dp)
                        Text(
                            text = errorSuperScript,
                            fontSize = fontSize,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else {
                Text(
                    text = label,
                    fontSize = fontSize
                )
            }
        }

        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(24.dp),
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Box(
                    modifier = modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp
                    )
                }
            },
            visualTransformation = visualTransformation,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                focusedContainerColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary
            ),
            trailingIcon = trailingIcon
        )
    }

    @Composable
    internal fun UIButton(
        modifier: Modifier = Modifier,
        text: String,
        contentColor: Color? = null,
        logo: (@Composable () -> Unit)? = null,
        primary: Boolean = false,
        onClick: () -> Unit = {}
    ) {
        Button(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp),
            onClick = onClick,
            colors = ButtonDefaults.buttonColors()
                .copy(
                    containerColor = if (primary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    contentColor = contentColor
                        ?: if (primary) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                )
        ) {
            Text(
                text = text,
                fontSize = 18.sp
            )
            if (logo != null) {
                VerticalDivider(thickness = 8.dp)
                logo()
            }
        }
    }

    @Composable
    internal fun ForgotPassword(modifier: Modifier = Modifier, fontSize: TextUnit = 11.sp, onResetClick: () -> Unit = { }) {
        Row(
            modifier = modifier
                .height(36.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Forgotten your password? ",
                fontSize = fontSize
            )
            Text(
                modifier = modifier
                    .clickable(onClick = onResetClick),
                text = "Reset Password",
                fontSize = fontSize,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
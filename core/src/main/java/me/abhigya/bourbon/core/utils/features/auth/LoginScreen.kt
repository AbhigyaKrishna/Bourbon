package me.abhigya.bourbon.core.utils.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.abhigya.bourbon.core.R

@Composable
fun LoginScreen() {
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
            Logo()
            Separator()
            InputField(
                label = "Email",
                placeholder = "Enter Email"
            )
            Separator()
            InputField(
                label = "Password",
                placeholder = "Enter Password",
                errorSuperScript = "Wrong Password",
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = {
                    Button(
                        modifier = Modifier
                            .size(24.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors()
                            .copy(
                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.0f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                        onClick = {}
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(width = 18.dp, height = 12.dp),
                            painter = painterResource(id = R.drawable.ic_eye),
                            contentDescription = "Show Password",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
            Separator()
            UIButton(
                text = "Log In",
                primary = true
            )
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
            UIButton(text = "Sign In")
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
            Row(
                modifier = Modifier
                    .height(36.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Forgotten your password? ",
                    fontSize = 11.sp
                )
                Button(
                    modifier = Modifier
                        .height(25.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors()
                        .copy(
                            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.0f),
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                    onClick = {}
                ) {
                    Text(
                        text = "Reset Password",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = "Logo",
        modifier = modifier
    )
}

@Composable
fun Separator(modifier: Modifier = Modifier, thickness: Dp = 24.dp) {
    HorizontalDivider(
        thickness = thickness,
        modifier = modifier
    )
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String = label,
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
        value = "",
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
fun UIButton(
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
                contentColor = contentColor ?: if (primary) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
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
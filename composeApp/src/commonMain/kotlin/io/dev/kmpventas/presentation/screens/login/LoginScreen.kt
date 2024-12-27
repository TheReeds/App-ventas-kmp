package io.dev.kmpventas.presentation.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.dev.kmpventas.domain.model.User
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.dev.kmpventas.presentation.components.AppButton
import io.dev.kmpventas.presentation.components.AppCard
import io.dev.kmpventas.presentation.components.AppTextFieldWithKeyboard
import io.dev.kmpventas.presentation.layouts.FormScreenLayout
import io.dev.kmpventas.presentation.theme.AppTheme
import io.dev.kmpventas.presentation.theme.LocalAppDimens
import io.dev.kmpventas.presentation.theme.ThemeViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    viewModel: LoginViewModel = koinInject()
) {
    val themeViewModel: ThemeViewModel = koinInject()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val validateEmail = {
        isEmailError = !email.contains("") || !email.contains(".")
        !isEmailError
    }

    val validatePassword = {
        isPasswordError = password.length < 6
        !isPasswordError
    }

    val validateAndLogin = {
        if (validateEmail() && validatePassword()) {
            keyboardController?.hide()
            viewModel.login(email, password)
        }
    }

    AppTheme(darkTheme = isDarkMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = LocalAppDimens.current.spacing_24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 400.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo con color adaptativo
                    AppCard(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = LocalAppDimens.current.spacing_32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VPM",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Botón de cambio de tema
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(onClick = { themeViewModel.toggleTheme() }) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (isDarkMode) "Cambiar a modo claro" else "Cambiar a modo oscuro",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Email TextField
                    AppTextFieldWithKeyboard(
                        value = email,
                        onValueChange = {
                            email = it
                            if (isEmailError) validateEmail()
                        },
                        label = "Correo electrónico",
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = if (isEmailError) {
                            {
                                Icon(
                                    Icons.Default.Error,
                                    null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        } else null,
                        isError = isEmailError,
                        errorMessage = if (isEmailError) "Ingrese un correo electrónico válido" else null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.padding(bottom = LocalAppDimens.current.spacing_16.dp)
                    )

                    // Password TextField (con colores adaptativos)
                    AppTextFieldWithKeyboard(
                        value = password,
                        onValueChange = {
                            password = it
                            if (isPasswordError) validatePassword()
                        },
                        label = "Contraseña",
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = isPasswordError,
                        errorMessage = if (isPasswordError) "La contraseña debe tener al menos 6 caracteres" else null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { validateAndLogin() }
                        ),
                        modifier = Modifier.padding(bottom = LocalAppDimens.current.spacing_24.dp)
                    )

                    AppButton(
                        text = "INICIAR SESIÓN",
                        onClick = { validateAndLogin() },
                        enabled = email.isNotEmpty() && password.isNotEmpty(),
                        loading = loginState is LoginState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = LocalAppDimens.current.spacing_16.dp)
                    )

                    // Error Message con colores adaptativos
                    AnimatedVisibility(
                        visible = loginState is LoginState.Error,
                        modifier = Modifier.padding(bottom = LocalAppDimens.current.spacing_16.dp)
                    ) {
                        AppCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = (loginState as? LoginState.Error)?.message ?: "",
                                modifier = Modifier.padding(LocalAppDimens.current.spacing_16.dp),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    TextButton(onClick = { /* TODO: Implementar recuperación */ }) {
                        Text(
                            "¿Olvidaste tu contraseña?",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

        // Handle login success
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess((loginState as LoginState.Success).user)
        }
    }
}
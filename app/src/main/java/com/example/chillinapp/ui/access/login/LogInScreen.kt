package com.example.chillinapp.ui.access.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.access.AccessHeader
import com.example.chillinapp.ui.access.utility.AccessStatus
import com.example.chillinapp.ui.access.utility.EmailSupportingText
import com.example.chillinapp.ui.access.utility.EmailValidationResult
import com.example.chillinapp.ui.access.utility.PasswordSupportingText
import com.example.chillinapp.ui.access.utility.PasswordValidationResult
import com.example.chillinapp.ui.access.utility.SimpleNotification
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme

object LogInDestination : NavigationDestination {
    override val route = "Login"
    override val titleRes = R.string.login_title
}

@Composable
fun LogInScreen(
    modifier: Modifier = Modifier,
    navigateToSignInScreen: () -> Unit = {},
    navigateToPswRecoveryScreen: () -> Unit = {},
    navigateToHomeScreen: (String) -> Unit = {},
    logInViewModel: LogInViewModel = viewModel()
) {

    val logInUiState by logInViewModel.uiState.collectAsState()

    Surface (
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(28.dp)
            .verticalScroll(rememberScrollState())
            .height(IntrinsicSize.Max)

    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AccessHeader(
                titleRes = LogInDestination.titleRes,
                title = stringResource(R.string.login_header)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LogInCard(
                    logInUiState = logInUiState,
                    logInViewModel = logInViewModel,
                    navigateToPswRecoveryScreen = navigateToPswRecoveryScreen
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = stringResource(R.string.don_t_have_an_account),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Light
                    )

                    Spacer(modifier = Modifier.size(4.dp))

                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    textDecoration = TextDecoration.Underline,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                append(stringResource(R.string.registration_link))
                            }
                        },
                        onClick = { navigateToSignInScreen() },
                    )

                }

            }

        }
    }

    when (logInUiState.logInStatus) {
        AccessStatus.SUCCESS -> {
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxSize()
            ) {
                Snackbar(
                    action = {
                        TextButton(
                            onClick = { navigateToHomeScreen(logInUiState.email) }
                        ) {
                            Text(stringResource(R.string.home_link))
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(text = stringResource(R.string.login_notify_success_text))
                }
            }
            navigateToHomeScreen(logInUiState.email)
        }
        AccessStatus.FAILURE -> {
            SimpleNotification(
                action = { logInViewModel.idleAccessStatus() },
                buttonText = stringResource(R.string.hide_notify_action),
                bodyText = stringResource(R.string.login_notify_failure_text)
            )
        }
        else -> { }
    }

}



@Composable
fun LogInCard(
    logInUiState: LogInUiState,
    logInViewModel: LogInViewModel,
    navigateToPswRecoveryScreen: () -> Unit = {}
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
        ) {

            val outlineTextFieldModifier: Modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)

            OutlinedTextField(
                value = logInUiState.email,
                onValueChange = { logInViewModel.updateEmail(it) },
                label = {
                    Text(stringResource(R.string.email_label))
                },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = stringResource(R.string.email_label))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = logInUiState.emailStatus != EmailValidationResult.VALID &&
                        logInUiState.emailStatus != EmailValidationResult.IDLE,
                supportingText = { EmailSupportingText(logInUiState.emailStatus) },
                modifier = outlineTextFieldModifier
            )

            OutlinedTextField(
                value = logInUiState.password,
                onValueChange = { logInViewModel.updatePassword(it) },
                label = {
                    Text(stringResource(R.string.password_label))
                },
                visualTransformation = if (logInUiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = stringResource(R.string.password_label))
                },
                trailingIcon = {
                    val image = if (logInUiState.isPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (logInUiState.isPasswordVisible) {
                        stringResource(R.string.hide_password_description)
                    } else {
                        stringResource(R.string.show_password_description)
                    }

                    IconButton(onClick = { logInViewModel.togglePasswordVisibility() }) {
                        Icon(image, contentDescription = description)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                isError = logInUiState.passwordStatus != PasswordValidationResult.VALID &&
                        logInUiState.passwordStatus != PasswordValidationResult.IDLE,
                supportingText = { PasswordSupportingText(logInUiState.passwordStatus) },
                modifier = outlineTextFieldModifier
            )

            Spacer(modifier = Modifier.size(16.dp))

            ClickableText(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        append(stringResource(R.string.password_recovery_link))
                    }
                },
                onClick = { navigateToPswRecoveryScreen() },
            )

            Spacer(modifier = Modifier.size(28.dp))

            Button(
                onClick = { logInViewModel.inputLogin() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = logInUiState.isLogInButtonEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.login_button_text),
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
            }

        }

        Spacer(modifier = Modifier.size(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ){
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
            )
            Text(
                text = stringResource(R.string.divider_or),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            onClick = { logInViewModel.googleLogin() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = stringResource(R.string.google_logo),
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.in_text_divider)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.login_button_text) +
                            " " +
                            stringResource(R.string.with_google_button_text),
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LightThemePreview() {
    ChillInAppTheme {
        LogInScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun DarkThemePreview() {
    ChillInAppTheme(useDarkTheme = true) {
        LogInScreen()
    }
}
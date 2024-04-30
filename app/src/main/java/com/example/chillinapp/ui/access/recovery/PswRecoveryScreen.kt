package com.example.chillinapp.ui.access.recovery

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.access.AccessHeader
import com.example.chillinapp.ui.access.utility.EmailSupportingText
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import com.example.chillinapp.ui.access.utility.SimpleNotification
import com.example.chillinapp.ui.access.utility.accessResultText
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme


/**
 * Object representing the password recovery screen destination.
 * It contains the route and title for the password recovery screen.
 */
object PswRecoveryDestination : NavigationDestination {
    override val route = "PasswordRecovery"
    override val titleRes = R.string.password_recovery
}

/**
 * Composable function for the password recovery screen.
 * It contains UI elements for password recovery, including a text field for email input and a button for password recovery.
 * It also handles navigation to the login and sign-in screens.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param navigateToLogInScreen Function to navigate to the login screen.
 * @param navigateToSignInScreen Function to navigate to the sign-in screen.
 * @param pswRecoveryViewModel ViewModel for the password recovery screen.
 */
@Composable
fun PswRecoveryScreen(
    modifier: Modifier = Modifier,
    navigateToLogInScreen: () -> Unit = {},
    navigateToSignInScreen: () -> Unit = {},
    pswRecoveryViewModel: PswRecoveryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val pswRecoveryUiState by pswRecoveryViewModel.uiState.collectAsState()

    // Surface for the password recovery screen
    Surface (
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(28.dp)
            .verticalScroll(rememberScrollState())
            .height(IntrinsicSize.Max)
    ) {

        // Column for the password recovery screen layout
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            // Header for the password recovery screen
            AccessHeader(
                titleRes = PswRecoveryDestination.titleRes,
                title = stringResource(R.string.password_recovery_header)
            )

            // Column for the password recovery screen layout
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ){

                // Column for the password recovery screen layout
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Text field for email input
                    OutlinedTextField(
                        value = pswRecoveryUiState.email,
                        enabled = pswRecoveryUiState.isLoading.not(),
                        onValueChange = { pswRecoveryViewModel.updateEmail(it) },
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
                        isError = pswRecoveryUiState.emailStatus != EmailValidationResult.VALID &&
                                pswRecoveryUiState.emailStatus != EmailValidationResult.IDLE,
                        supportingText = { EmailSupportingText(pswRecoveryUiState.emailStatus) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    // Button for password recovery
                    Button(
                        onClick = { pswRecoveryViewModel.recover() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        enabled = pswRecoveryUiState.isButtonEnabled && pswRecoveryUiState.isLoading.not(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.password_recovery_button_text),
                            fontSize = MaterialTheme.typography.labelLarge.fontSize
                        )
                    }
                }

                Spacer(modifier = Modifier.size(28.dp))

                // Column for the password recovery screen layout
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    // Row for the password recovery screen layout
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        // Text for the password recovery screen
                        Text(
                            text = stringResource(R.string.don_t_have_an_account),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Light
                        )

                        Spacer(modifier = Modifier.size(4.dp))

                        // Clickable text for the password recovery screen
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
                            onClick = { if(pswRecoveryUiState.isLoading.not()) navigateToSignInScreen() },
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    // Row for the password recovery screen layout
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        // Text for the password recovery screen
                        Text(
                            text = stringResource(R.string.already_have_an_account),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Light
                        )

                        Spacer(modifier = Modifier.size(4.dp))

                        // Clickable text for the password recovery screen
                        ClickableText(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        textDecoration = TextDecoration.Underline,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    append(stringResource(R.string.login_link))
                                }
                            },
                            onClick = { if(pswRecoveryUiState.isLoading.not()) navigateToLogInScreen() },
                        )
                    }
                }
            }
        }
    }

    // Handling the password recovery result
    when (pswRecoveryUiState.recoveryResult?.success) {
        true -> {
            // Notification for successful password recovery
            SimpleNotification(
                action = { navigateToLogInScreen() },
                buttonText = stringResource(id = R.string.login_link),
                bodyText = stringResource(R.string.password_recovery_notify_success_text)
            )
        }
        false -> {
            // Notification for unsuccessful password recovery
            SimpleNotification(
                action = { pswRecoveryViewModel.idleResult() },
                buttonText = stringResource(id = R.string.hide_notify_action),
                bodyText = accessResultText(pswRecoveryUiState.recoveryResult)
            )
        }
        else -> { }
    }

}

/**
 * Preview function for the light theme of the password recovery screen.
 */
@Preview(showBackground = true)
@Composable
fun LightThemePreview() {
    ChillInAppTheme {
        PswRecoveryScreen()
    }
}

/**
 * Preview function for the dark theme of the password recovery screen.
 */
@Preview(showBackground = true)
@Composable
fun DarkThemePreview() {
    ChillInAppTheme(useDarkTheme = true) {
        PswRecoveryScreen()
    }
}
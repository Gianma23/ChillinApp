package com.example.chillinapp.ui.access.registration

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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.access.AccessHeader
import com.example.chillinapp.ui.access.utility.ConfirmPasswordSupportingText
import com.example.chillinapp.ui.access.utility.validationResult.ConfirmPasswordValidationResult
import com.example.chillinapp.ui.access.utility.EmailSupportingText
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import com.example.chillinapp.ui.access.utility.NameSupportingText
import com.example.chillinapp.ui.access.utility.validationResult.NameValidationResult
import com.example.chillinapp.ui.access.utility.SimpleNotification
import com.example.chillinapp.ui.access.utility.PasswordSupportingText
import com.example.chillinapp.ui.access.utility.accessResultText
import com.example.chillinapp.ui.access.utility.validationResult.PasswordValidationResult
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme


/**
 * Object representing the sign-in screen destination.
 * It contains the route and title for the sign-in screen.
 */
object SignInDestination : NavigationDestination {
    override val route = "Register"
    override val titleRes = R.string.registration_string
}

/**
 * Composable function for the sign-in screen.
 * It contains UI elements for sign-in, including text fields for name, email, password and confirm password input, and buttons for sign-in and Google sign-in.
 * It also handles navigation to the login screen.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param navigateToLogInScreen Function to navigate to the login screen.
 * @param signInViewModel ViewModel for the sign-in screen.
 */
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navigateToLogInScreen: () -> Unit = {},
    signInViewModel: SignInViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    // Collect the UI state from the sign-in view model
    val signInUiState by signInViewModel.uiState.collectAsState()

    // Surface for the sign-in screen
    Surface (
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(28.dp)
            .verticalScroll(rememberScrollState())
            .height(IntrinsicSize.Max)

    ) {

        // Column for the sign-in screen layout
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header for the sign-in screen
            AccessHeader(
                titleRes = SignInDestination.titleRes,
                title = stringResource(R.string.registration_header)
            )

            // Column for the sign-in screen layout
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Card for the sign-in screen layout
                SignInCard(
                    signInUiState = signInUiState,
                    signInViewModel = signInViewModel
                )

                // Row for the sign-in screen layout
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    // Text for the sign-in screen
                    Text(
                        text = stringResource(R.string.already_have_an_account),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Light
                    )

                    Spacer(modifier = Modifier.size(4.dp))

                    // Clickable text for the sign-in screen
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
                        onClick = { if(signInUiState.isLoading.not()) navigateToLogInScreen() }
                    )

                }

            }

        }
    }

    // Handling the sign-in result
    when (signInUiState.registrationResult?.success) {
        true -> {
            // Notification for successful sign-in
            SimpleNotification(
                action = { navigateToLogInScreen() },
                buttonText = stringResource(id = R.string.login_link),
                bodyText = stringResource(R.string.registration_notify_success_text)
            )
        }
        false -> {
            // Notification for unsuccessful sign-in
            SimpleNotification(
                action = { signInViewModel.idleResult() },
                buttonText = stringResource(id = R.string.hide_notify_action),
                bodyText = accessResultText(signInUiState.registrationResult)
            )
        }
        else -> { }
    }

}

/**
 * Composable function for the sign-in card.
 * It contains UI elements for sign-in, including text fields for name, email, password and confirm password input, and buttons for sign-in and Google sign-in.
 *
 * @param signInUiState The UI state for the sign-in screen.
 * @param signInViewModel ViewModel for the sign-in screen.
 */
@Composable
fun SignInCard(
    signInUiState: SignInUiState,
    signInViewModel: SignInViewModel
){

    // Card for the sign-in screen layout
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {

        // Column for the sign-in card layout
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
        ) {

            // Modifier for the outlined text fields
            val outlineTextFieldModifier: Modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)

            // Outlined text field for name input
            OutlinedTextField(
                value = signInUiState.account.name ?: "",
                enabled = signInUiState.isLoading.not(),
                onValueChange = { signInViewModel.updateName(it) },
                label = {
                    Text(stringResource(R.string.name_label))
                },
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.name_label))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = signInUiState.nameStatus != NameValidationResult.VALID &&
                        signInUiState.nameStatus != NameValidationResult.IDLE,
                supportingText = { NameSupportingText(signInUiState.nameStatus) },
                modifier = outlineTextFieldModifier
            )

            // Outlined text field for email input
            OutlinedTextField(
                value = signInUiState.account.email ?: "",
                enabled = signInUiState.isLoading.not(),
                onValueChange = { signInViewModel.updateEmail(it) },
                label = {
                    Text(stringResource(id = R.string.email_label))
                },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = stringResource(id = R.string.email_label))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = signInUiState.emailStatus != EmailValidationResult.VALID &&
                        signInUiState.emailStatus != EmailValidationResult.IDLE,
                supportingText = { EmailSupportingText(signInUiState.emailStatus) },
                modifier = outlineTextFieldModifier
            )

            // Outlined text field for password input
            OutlinedTextField(
                value = signInUiState.account.password ?: "",
                enabled = signInUiState.isLoading.not(),
                onValueChange = { signInViewModel.updatePassword(it) },
                label = {
                    Text(stringResource(id = R.string.password_label))
                },
                visualTransformation = if (signInUiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = stringResource(id = R.string.password_label))
                },
                trailingIcon = {
                    val image = if (!signInUiState.isPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (signInUiState.isPasswordVisible) {
                        stringResource(R.string.hide_password_description)
                    } else {
                        stringResource(R.string.show_password_description)
                    }
                    IconButton(onClick = { signInViewModel.togglePasswordVisibility() }) {
                        Icon(image, contentDescription = description)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = signInUiState.passwordStatus != PasswordValidationResult.VALID &&
                        signInUiState.passwordStatus != PasswordValidationResult.IDLE,
                supportingText = { PasswordSupportingText(signInUiState.passwordStatus) },
                modifier = outlineTextFieldModifier
            )

            // Outlined text field for confirm password input
            OutlinedTextField(
                value = signInUiState.confirmPassword,
                enabled = signInUiState.isLoading.not(),
                onValueChange = { signInViewModel.updateConfirmPassword(it) },
                label = {
                    Text(stringResource(R.string.confirm_password_label))
                },
                visualTransformation = if (signInUiState.isConfirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = stringResource(R.string.confirm_password_label))
                },
                trailingIcon = {
                    val image = if (!signInUiState.isConfirmPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (signInUiState.isConfirmPasswordVisible) {
                        stringResource(R.string.hide_confirm_password_description)
                    } else {
                        stringResource(R.string.show_confirm_password_description)
                    }
                    IconButton(onClick = { signInViewModel.toggleConfirmPasswordVisibility() }) {
                        Icon(image, contentDescription = description)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                isError = signInUiState.confirmPasswordStatus != ConfirmPasswordValidationResult.VALID &&
                        signInUiState.confirmPasswordStatus != ConfirmPasswordValidationResult.IDLE,
                supportingText = { ConfirmPasswordSupportingText(signInUiState.confirmPasswordStatus)},
                modifier = outlineTextFieldModifier
            )

            Spacer(modifier = Modifier.size(28.dp))

            // Button for sign-in
            Button(
                onClick = { signInViewModel.signIn() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = signInUiState.isSignUpButtonEnabled && signInUiState.isLoading.not(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.registration_button_text),
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
            }

        }

        Spacer(modifier = Modifier.size(16.dp))

        // Divider for the sign-in screen
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

        // Button for Google sign-in
        Button(
            onClick = { signInViewModel.googleSignIn() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            enabled = signInUiState.isLoading.not(),
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
                    text = stringResource(R.string.registration_button_text) +
                            " " +
                            stringResource(R.string.with_google_button_text),
                )

            }
        }
    }
}

/**
 * Preview for the light theme of the sign-in screen.
 */
@Preview(showBackground = true)
@Composable
fun LightThemePreview() {
    ChillInAppTheme {
        SignInScreen()
    }
}

/**
 * Preview for the dark theme of the sign-in screen.
 */
@Preview(showBackground = true)
@Composable
fun DarkThemePreview() {
    ChillInAppTheme(useDarkTheme = true) {
        SignInScreen()
    }
}
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
import androidx.compose.material3.Divider
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
import com.example.chillinapp.ui.access.AccessStatus
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme

object SignInDestination : NavigationDestination {
    override val route = "Register"
    override val titleRes = R.string.registration_string
}

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navigateToLogInScreen: () -> Unit = {},
    signInViewModel: SignInViewModel = viewModel()
) {

    val signInUiState by signInViewModel.uiState.collectAsState()

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
                titleRes = SignInDestination.titleRes,
                title = "Create an Account"
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                SignInCard(
                    signInUiState = signInUiState,
                    signInViewModel = signInViewModel
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Already have an account?",
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
                                append("Log in")
                            }
                        },
                        onClick = { navigateToLogInScreen() }
                    )

                }

            }

        }
    }

    Notification(
        status = signInUiState.registrationStatus,
        successAction = navigateToLogInScreen,
        successButton = "Login",
        successText = "Registration successful!",
        failAction = { signInViewModel.idleAccessStatus() },
        failButton = "Dismiss",
        failText = "Registration failed! Please try again."
    )

}

@Composable
fun Notification(
    status: AccessStatus = AccessStatus.IDLE,
    successAction: () -> Unit = {},
    successButton: String = "Dismiss",
    successText: String = "Success!",
    failAction: () -> Unit = {},
    failButton: String = "Dismiss",
    failText: String = "Failure!"
){
    when (status) {
        AccessStatus.SUCCESS -> {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxSize()
            ) {
                Snackbar(
                    action = {
                        TextButton(
                            onClick = { successAction() }
                        ) {
                            Text(text = successButton)
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(text = successText)
                }
            }
        }
        AccessStatus.FAILURE -> {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxSize()
            ) {
                Snackbar(
                    action = {
                        TextButton(
                            onClick = { failAction() }
                        ) {
                            Text(text = failButton)
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(text = failText)
                }
            }
        }
        else -> { }
    }
}

@Composable
fun SignInCard(
    signInUiState: SignInUiState,
    signInViewModel: SignInViewModel
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
                .padding(top = 4.dp)

            OutlinedTextField(
                value = signInUiState.name,
                onValueChange = { signInViewModel.updateName(it) },
                label = {
                    Text("Name")
                },
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = "First name")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = !signInUiState.isNameValid,
                supportingText = {
                    if (!signInUiState.isNameValid) {
                        Text(
                            text = signInUiState.nameErrorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = outlineTextFieldModifier
            )

            OutlinedTextField(
                value = signInUiState.email,
                onValueChange = { signInViewModel.updateEmail(it) },
                label = {
                    Text("Email")
                },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = "Email")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = !signInUiState.isEmailValid,
                supportingText = {
                    if (!signInUiState.isEmailValid) {
                        Text(
                            text = signInUiState.emailErrorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = outlineTextFieldModifier
            )

            OutlinedTextField(
                value = signInUiState.password,
                onValueChange = { signInViewModel.updatePassword(it) },
                label = {
                    Text("Password")
                },
                visualTransformation = if (signInUiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = "Password")
                },
                trailingIcon = {
                    val image = if (!signInUiState.isPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { signInViewModel.togglePasswordVisibility() }) {
                        Icon(image, contentDescription = "Show Password")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = !signInUiState.isPasswordValid,
                supportingText = {
                    if (!signInUiState.isPasswordValid) {
                        Text(
                            text = signInUiState.passwordErrorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = outlineTextFieldModifier
            )


            OutlinedTextField(
                value = signInUiState.confirmPassword,
                onValueChange = { signInViewModel.updateConfirmPassword(it) },
                label = {
                    Text("Confirm password")
                },
                visualTransformation = if (signInUiState.isConfirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = "Confirm password")
                },
                trailingIcon = {
                    val image = if (!signInUiState.isPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { signInViewModel.toggleConfirmPasswordVisibility() }) {
                        Icon(image, contentDescription = "Show Confirm Password")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                isError = !signInUiState.isConfirmPasswordValid,
                supportingText = {
                    if (!signInUiState.isConfirmPasswordValid) {
                        Text(
                            text = signInUiState.confirmPasswordErrorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = outlineTextFieldModifier
            )

            Spacer(modifier = Modifier.size(28.dp))

            Button(
                onClick = { signInViewModel.inputSignIn() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = signInUiState.isSignUpButtonEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sign up",
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
            }

        }

        Spacer(modifier = Modifier.size(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ){
            Divider(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
            )
            Text(
                text = "or",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
            Divider(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Button(
            onClick = { signInViewModel.googleSignIn() },
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
                    text = "G",
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "|"
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Sign in with Google"
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LightThemePreview() {
    ChillInAppTheme {
        SignInScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun DarkThemePreview() {
    ChillInAppTheme(useDarkTheme = true) {
        SignInScreen()
    }
}
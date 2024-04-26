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

object PswRecoveryDestination : NavigationDestination {
    override val route = "PasswordRecovery"
    override val titleRes = R.string.password_recovery
}

@Composable
fun PswRecoveryScreen(
    modifier: Modifier = Modifier,
    navigateToLogInScreen: () -> Unit = {},
    navigateToSignInScreen: () -> Unit = {},
    pswRecoveryViewModel: PswRecoveryViewModel = viewModel()
) {

    val pswRecoveryUiState by pswRecoveryViewModel.uiState.collectAsState()

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
        ){

            AccessHeader(
                titleRes = PswRecoveryDestination.titleRes,
                title = "Change password"
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(
                        value = pswRecoveryUiState.email,
                        onValueChange = { pswRecoveryViewModel.updateEmail(it) },
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
                        isError = !pswRecoveryUiState.isEmailValid,
                        supportingText = {
                            if (!pswRecoveryUiState.isEmailValid) {
                                Text(
                                    text = "Please enter a valid email address",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Button(
                        onClick = { pswRecoveryViewModel.recover() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        enabled = pswRecoveryUiState.isButtonEnabled,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Recover password",
                            fontSize = MaterialTheme.typography.labelLarge.fontSize
                        )
                    }
                }

                Spacer(modifier = Modifier.size(28.dp))

                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "Don't have an account?",
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
                                    append("Sign Up")
                                }
                            },
                            onClick = { navigateToSignInScreen() },
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

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
                            onClick = { navigateToLogInScreen() },
                        )
                    }
                }
            }
        }
    }


    when (pswRecoveryUiState.recoveryStatus) {
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
                            onClick = { navigateToLogInScreen() }
                        ) {
                            Text("Login")
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(text = "Password recovery successful!")
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
                            onClick = { pswRecoveryViewModel.idleAccessStatus() }
                        ) {
                            Text("Dismiss")
                        }
                    },
                    modifier = Modifier
                ){
                    Text("Recovery failed! Please try again.")
                }
            }
        }
        else -> { }
    }
}

@Preview(showBackground = true)
@Composable
fun LightThemePreview() {
    ChillInAppTheme {
        PswRecoveryScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun DarkThemePreview() {
    ChillInAppTheme(useDarkTheme = true) {
        PswRecoveryScreen()
    }
}
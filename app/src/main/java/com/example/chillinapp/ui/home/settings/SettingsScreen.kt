package com.example.chillinapp.ui.home.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chillinapp.R
import com.example.chillinapp.ui.AppViewModelProvider
import com.example.chillinapp.ui.SimpleNotification
import com.example.chillinapp.ui.accessErrorText
import com.example.chillinapp.ui.navigation.NavigationDestination
import com.example.chillinapp.ui.theme.ChillInAppTheme

/**
 * Object representing the settings destination in the navigation.
 */
object SettingsDestination : NavigationDestination {
    override val route: String = "settings"
    override val titleRes: Int = R.string.settings_title
}

/**
 * A Composable function that represents the settings screen.
 *
 * @param modifier The modifier to be applied to the settings screen, default value is Modifier.
 * @param onLogOut The function to be executed when the user logs out, default value is an empty function.
 * @param onDeletingAccount The function to be executed when the user deletes their account, default value is an empty function.
 * @param viewModel The view model for the settings screen, default value is the view model from the AppViewModelProvider factory.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onLogOut: () -> Unit = {},
    onDeletingAccount: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = if(uiState.name != null) {
                        "Hi, ${uiState.name}"
                    } else {
                        stringResource(id = R.string.settings_title)
                    },
                    style = MaterialTheme.typography.displaySmall,
                )
                Text(
                    text = if(uiState.email != null) {
                        "Email: ${uiState.email}"
                    } else {
                        ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface,
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            SettingsGroup(
                title = R.string.general_settings_label
            ){
                SettingsItem(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    iconDesc = R.string.log_out_label,
                    name = R.string.log_out_label,
                    onClick = { viewModel.onLogOut() },
                    enabled = !uiState.loadingOperation
                )
            }

            SettingsGroup(
                title = R.string.danger_zone_setting_label,
                isSensitive = true
            ){
                SettingsItem(
                    icon = Icons.Outlined.DeleteOutline,
                    iconDesc = R.string.delete_account_label,
                    name = R.string.delete_account_label,
                    onClick = { viewModel.toggleDeleteAccountDialog() },
                    enabled = !uiState.loadingOperation,
                    isSensitive = true
                )
            }
        }
    }

    if(uiState.isDeleteAccountDialogOpened){
        AlertDialog(
            onDismissRequest = { viewModel.toggleDeleteAccountDialog() },
            title = { Text(text = stringResource(R.string.delete_account_confirmation_title)) },
            text = { Text(text = stringResource(R.string.delete_account_confirmation_text)) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onConfirmDeleteAccount() },
                    enabled = !uiState.loadingOperation
                ) {
                    Text(text = stringResource(R.string.confirm_button_label))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.toggleDeleteAccountDialog() },
                    enabled = !uiState.loadingOperation
                ) {
                    Text(text = stringResource(R.string.cancel_button_label))
                }
            }
        )
    }

    if (uiState.logOutResponse?.success == true){
        onLogOut()
    } else if (uiState.logOutResponse?.error != null){
        SimpleNotification(
            action = { viewModel.toggleNotification() },
            buttonText = stringResource(id = R.string.hide_notify_action),
            bodyText = stringResource(R.string.operation_failed) +
                    accessErrorText(error = uiState.logOutResponse?.error)
        )
    }

    if (uiState.deleteAccountResponse?.success == true){
        onDeletingAccount()
    } else if (uiState.deleteAccountResponse?.error != null){
        SimpleNotification(
            action = { viewModel.toggleNotification() },
            buttonText = stringResource(id = R.string.hide_notify_action),
            bodyText = stringResource(R.string.operation_failed) +
                    accessErrorText(error = uiState.deleteAccountResponse?.error)
        )
    }

}

/**
 * A Composable function that represents a group of settings.
 *
 * @param modifier The modifier to be applied to the settings group, default value is Modifier.
 * @param isSensitive A boolean flag indicating whether the settings group is sensitive, default value is false.
 * @param title The title of the settings group.
 * @param content The content of the settings group.
 */
@Composable
fun SettingsGroup(
    modifier: Modifier = Modifier,
    isSensitive: Boolean = false,
    @StringRes title: Int,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
        )
        Card(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(0.dp),
            colors =
                if(isSensitive) {
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                } else {
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                }
        ) {
            content()
        }
    }
}

/**
 * A Composable function that represents a settings item.
 *
 * @param icon The icon of the settings item.
 * @param iconDesc The description of the icon.
 * @param name The name of the settings item.
 * @param modifier The modifier to be applied to the settings item, default value is Modifier.
 * @param onClick The function to be executed when the settings item is clicked, default value is an empty function.
 * @param enabled A boolean flag indicating whether the settings item is enabled, default value is true.
 * @param isSensitive A boolean flag indicating whether the settings item is sensitive, default value is false.
 */
@Composable
fun SettingsItem(
    icon: ImageVector,
    @StringRes iconDesc: Int,
    @StringRes name: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    isSensitive: Boolean = false
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = onClick,
        enabled = enabled,
        color =
            if(isSensitive) {
                    MaterialTheme.colorScheme.errorContainer
            } else {
                    MaterialTheme.colorScheme.surface
            },
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(id = iconDesc),
                        tint =
                            if(isSensitive) {
                                MaterialTheme.colorScheme.onErrorContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceTint
                            },
                        modifier = Modifier
                            .size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = name),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color =
                                if(isSensitive) {
                                    MaterialTheme.colorScheme.onErrorContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceTint
                                },
                        ),
                        modifier = Modifier
                            .padding(16.dp),
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    tint =
                        if(isSensitive) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceTint
                        },
                    contentDescription = null
                )
            }
            HorizontalDivider(
                color =
                    if(isSensitive) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceTint
                    },
            )
        }
    }
}

/**
 * A Composable function that represents a preview of the settings screen.
 */
@Preview
@Composable
fun SettingsScreenPreview(){
    ChillInAppTheme {
        SettingsScreen()
    }
}

/**
 * A Composable function that represents a preview of the settings screen in dark theme.
 */
@Preview
@Composable
fun SettingsScreenPreviewDarkTheme(){
    ChillInAppTheme(useDarkTheme = true) {
        SettingsScreen()
    }
}
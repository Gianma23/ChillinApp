package com.example.chillinapp.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.chillinapp.R
import com.example.chillinapp.data.account.AccountErrorType
import com.example.chillinapp.data.map.MapErrorType
import com.example.chillinapp.data.stress.StressErrorType
import com.example.chillinapp.ui.access.utility.validationResult.ConfirmPasswordValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.NameValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.PasswordValidationResult


/**
 * Composable function that displays a supporting text based on the validation status of the name input.
 *
 * @param status The validation status of the name input.
 */
@Composable
fun NameSupportingText(status: NameValidationResult){
    when(status){
        NameValidationResult.EMPTY -> SupportingErrorText(text = stringResource(R.string.empty_label))
        NameValidationResult.TOO_LONG -> SupportingErrorText(text = stringResource(R.string.too_long_label))
        NameValidationResult.INVALID_CHARACTERS -> SupportingErrorText(text = stringResource(R.string.invalid_characters_label))
        else -> { }
    }
}

/**
 * Composable function that displays a supporting text based on the validation status of the email input.
 *
 * @param emailStatus The validation status of the email input.
 */
@Composable
fun EmailSupportingText(emailStatus: EmailValidationResult) {
    when(emailStatus){
        EmailValidationResult.EMPTY -> SupportingErrorText(text = stringResource(R.string.empty_label))
        EmailValidationResult.TOO_LONG -> SupportingErrorText(text = stringResource(R.string.too_long_label))
        EmailValidationResult.DO_NOT_EXIST -> SupportingErrorText(text = stringResource(R.string.email_does_not_exist))
        EmailValidationResult.ALREADY_EXISTS -> SupportingErrorText(text = stringResource(R.string.email_already_exists))
        EmailValidationResult.INVALID_FORMAT -> SupportingErrorText(text = stringResource(R.string.invalid_format_label))
        else -> { }
    }
}

/**
 * Composable function that displays a supporting text based on the validation status of the password input.
 *
 * @param passwordStatus The validation status of the password input.
 */
@Composable
fun PasswordSupportingText(passwordStatus: PasswordValidationResult) {
    when(passwordStatus){
        PasswordValidationResult.EMPTY -> SupportingErrorText(text = stringResource(R.string.empty_label))
        PasswordValidationResult.TOO_SHORT -> SupportingErrorText(text = stringResource(R.string.too_short_label))
        PasswordValidationResult.TOO_LONG -> SupportingErrorText(text = stringResource(R.string.too_long_label))
        PasswordValidationResult.NO_DIGITS -> SupportingErrorText(text = stringResource(R.string.no_digit_label))
        PasswordValidationResult.NO_LOWERCASE -> SupportingErrorText(text = stringResource(R.string.no_lower_case_label))
        PasswordValidationResult.NO_UPPERCASE -> SupportingErrorText(text = stringResource(R.string.no_upper_case_label))
        PasswordValidationResult.NO_SPECIAL_CHAR -> SupportingErrorText(text = stringResource(R.string.no_special_character_label))
        else -> { }
    }
}

/**
 * Composable function that displays a supporting text based on the validation status of the confirm password input.
 *
 * @param confirmPasswordStatus The validation status of the confirm password input.
 */
@Composable
fun ConfirmPasswordSupportingText(confirmPasswordStatus: ConfirmPasswordValidationResult) {
    when(confirmPasswordStatus){
        ConfirmPasswordValidationResult.EMPTY -> SupportingErrorText(text = stringResource(R.string.empty_label))
        ConfirmPasswordValidationResult.NOT_MATCH -> SupportingErrorText(text = stringResource(R.string.no_match_label))
        else -> { }
    }
}

/**
 * Composable function that displays an physiologicalError text.
 *
 * @param text The physiologicalError text to be displayed.
 */
@Composable
fun SupportingErrorText(text: String){
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error
    )
}

/**
 * Composable function that returns a string based on the result of the account access.
 *
 * @param error The result of the account access.
 * @return A string representing the result of the account access.
 */
@Composable
fun accessErrorText(error: AccountErrorType?): String = when(error){
    AccountErrorType.EMAIL_IN_USE -> stringResource(R.string.email_already_exists)
    AccountErrorType.AUTHENTICATION_FAILED -> stringResource(R.string.authentication_failed)
    AccountErrorType.ACCOUNT_NOT_FOUND -> stringResource(R.string.account_not_found)
    AccountErrorType.NOT_YET_IMPLEMENTED -> stringResource(R.string.not_yet_implemented)
    AccountErrorType.INVALID_EMAIL -> stringResource(R.string.invalid_email)
    AccountErrorType.INVALID_PASSWORD -> stringResource(R.string.invalid_password)
    AccountErrorType.DATABASE_ERROR -> stringResource(R.string.database_error)
    AccountErrorType.AUTHENTICATION_ERROR -> stringResource(R.string.authentication_error)
    else -> stringResource(R.string.general_error)
}

@Composable
fun stressErrorText(error: StressErrorType?): String = when(error){
    StressErrorType.NOT_YET_IMPLEMENTED -> stringResource(R.string.not_yet_implemented)
    StressErrorType.NETWORK_ERROR -> stringResource(R.string.network_error)
    StressErrorType.COMMUNICATION_PROBLEM -> stringResource(R.string.communication_problem)
    StressErrorType.NO_ACCOUNT -> stringResource(R.string.account_not_found)
    else -> stringResource(R.string.general_error)
}

@Composable
fun mapErrorText(error: MapErrorType?): String = when(error){
    MapErrorType.NOT_YET_IMPLEMENTED -> stringResource(R.string.not_yet_implemented)
    MapErrorType.NETWORK_ERROR -> stringResource(R.string.network_error)
    MapErrorType.COMMUNICATION_PROBLEM -> stringResource(R.string.communication_problem)
    MapErrorType.NO_ACCOUNT -> stringResource(R.string.account_not_found)
    MapErrorType.NO_DATA -> stringResource(R.string.no_data_available_for_this_area)
    else -> stringResource(R.string.general_error)
}
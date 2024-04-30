package com.example.chillinapp.ui.access.registration

import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.account.Account
import com.example.chillinapp.data.account.AccountErrorType
import com.example.chillinapp.ui.access.utility.validationResult.ConfirmPasswordValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.EmailValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.NameValidationResult
import com.example.chillinapp.ui.access.utility.validationResult.PasswordValidationResult


/**
 * Data class representing the UI state for the sign-in screen.
 * It contains the account data, confirm password input, validation status for name, email, password and confirm password,
 * visibility status for password and confirm password, enabled status for the sign-up button, loading status, and registration result.
 *
 * @property account The account data in the sign-in screen.
 * @property confirmPassword The confirm password input in the sign-in screen.
 * @property nameStatus The validation status of the name input.
 * @property emailStatus The validation status of the email input.
 * @property passwordStatus The validation status of the password input.
 * @property confirmPasswordStatus The validation status of the confirm password input.
 * @property isPasswordVisible The visibility status of the password input.
 * @property isConfirmPasswordVisible The visibility status of the confirm password input.
 * @property isSignUpButtonEnabled The enabled status of the sign-up button.
 * @property isLoading The loading status of the sign-in process.
 * @property registrationResult The result of the sign-in process.
 */
data class SignInUiState (

    val account: Account = Account(),
    val confirmPassword: String = "",

    val nameStatus: NameValidationResult = NameValidationResult.IDLE,
    val emailStatus: EmailValidationResult = EmailValidationResult.IDLE,
    val passwordStatus: PasswordValidationResult = PasswordValidationResult.IDLE,
    val confirmPasswordStatus: ConfirmPasswordValidationResult = ConfirmPasswordValidationResult.IDLE,

    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    val isSignUpButtonEnabled: Boolean = false,

    val isLoading: Boolean = false,
    val registrationResult: ServiceResult<Unit, AccountErrorType>? = null

)
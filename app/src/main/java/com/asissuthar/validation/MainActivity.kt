package com.asissuthar.validation

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.asissuthar.validation.databinding.ActivityMainBinding
import com.asissuthar.validation.formfields.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import reactivecircus.flowbinding.android.view.clicks

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val api = object {
        fun isUsernameAvailable(value: String): Boolean {
            return true
        }
    }

    private val fieldUsername by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tilUsername,
            textInputEditText = binding.etUsername,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> "Username is required."
                    !api.isUsernameAvailable(value) -> "Username is not available."
                    else -> null
                }
            }
        )
    }
    private val fieldEmail by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tilEmail,
            textInputEditText = binding.etEmail,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> "Email is required."
                    !Patterns.EMAIL_ADDRESS.toRegex().matches(value) -> "Invalid email."
                    else -> null
                }
            }
        )
    }
    private val fieldPhoneNumber by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tilPhoneNumber,
            textInputEditText = binding.etPhoneNumber,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> "Phone number is required."
                    value.length != 10 -> "Invalid phone number."
                    else -> null
                }
            }
        )
    }
    private val fieldGender by lazy {
        FormFieldRadioGroup(
            scope = lifecycleScope,
            container = binding.llGender,
            radioGroup = binding.rgGender,
            list = listOf(
                binding.rbGenderMale,
                binding.rbGenderFemale,
                binding.rbGenderOther,
            ),
            errorTextView = binding.tvGenderError,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> "Gender is required."
                    else -> null
                }
            }
        )
    }
    private val fieldHobby by lazy {
        FormFieldCheckboxGroup(
            scope = lifecycleScope,
            container = binding.llHobby,
            list = listOf(
                binding.cbHobbyCoding,
                binding.cbHobbySports,
                binding.cbHobbyReading,
            ),
            errorTextView = binding.tvHobbyError,
            validation = { value ->
                when {
                    value.isNullOrEmpty() -> "Hobby is required."
                    value.size < 2 -> "Select at least 2 hobbies."
                    else -> null
                }
            }
        )
    }
    private val fieldPassword by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tilPassword,
            textInputEditText = binding.etPassword,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> "Password is required."
                    value.length <= 6 -> "Password length must be above 6 characters."
                    else -> null
                }
            }
        )
    }
    private val fieldConfirmPassword by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tilConfirmPassword,
            textInputEditText = binding.etConfirmPassword,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> "Confirm Password is required."
                    value != fieldPassword.value -> "Confirm Password must be same as Password."
                    else -> null
                }
            }
        )
    }

    private val formFields by lazy {
        listOf(
            fieldUsername,
            fieldEmail,
            fieldPhoneNumber,
            fieldGender,
            fieldHobby,
            fieldPassword,
            fieldConfirmPassword,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        formFields

        binding.btnSubmit.clicks().onEach {
            submit()
        }.launchIn(lifecycleScope)
    }

    private fun submit() = lifecycleScope.launch {
        binding.btnSubmit.isEnabled = false

        formFields.disable()
        if (formFields.validate()) {

            // Use field data here
            val username = fieldUsername.value
            val email = fieldEmail.value
            val phoneNumber = fieldPhoneNumber.value
            val selectedGender = fieldGender.value
            val selectedHobbies = fieldHobby.value
            val password = fieldPassword.value

            println("$username, $email, $phoneNumber, $selectedGender, $selectedHobbies, $password")

            showToast("Submit successful!")
        }
        formFields.enable()

        binding.btnSubmit.isEnabled = true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
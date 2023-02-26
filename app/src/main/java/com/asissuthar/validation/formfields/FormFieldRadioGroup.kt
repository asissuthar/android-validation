package com.asissuthar.validation.formfields

import android.view.View
import android.widget.RadioGroup
import androidx.core.view.isVisible
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import reactivecircus.flowbinding.android.widget.checkedChanges

class FormFieldRadioGroup(
    scope: CoroutineScope,
    private val container: View,
    private val radioGroup: RadioGroup,
    private val list: List<MaterialRadioButton>,
    private val errorTextView: MaterialTextView,
    private val validation: suspend (String?) -> String? = { null },
) : FormField<String>() {

    var isEnabled: Boolean
        get() = list.any { it.isEnabled }
        set(value) {
            list.forEach { it.isEnabled = value }
        }

    var isVisible: Boolean
        get() = container.isVisible
        set(value) {
            container.isVisible = value
        }

    var value: String?
        get() = stateInternal.value
        set(value) {
            radioGroup.check(list.firstOrNull { it.text == value }?.id ?: -1)
        }

    init {
        radioGroup.checkedChanges().onEach { checkedId ->
            clearError()
            stateInternal.update { list.firstOrNull { it.id == checkedId }?.text?.toString() }
        }.launchIn(scope)
    }

    override fun clearError() {
        if (errorTextView.isVisible) {
            errorTextView.isVisible = false
        }
    }

    override fun clearFocus() {
        container.clearFocus()
    }

    override fun disable() {
        isEnabled = false
    }

    override fun enable() {
        isEnabled = true
    }

    override suspend fun validate(focusIfError: Boolean): Boolean {
        if (!isVisible) {
            return true
        }
        val errorValue = try {
            validation(stateInternal.value)
        } catch (error: Throwable) {
            error.message
        }
        val result = errorValue == null
        if (result) {
            clearError()
        } else {
            errorTextView.isVisible = true
            errorTextView.text = errorValue
            if (focusIfError) {
                container.requestFocus()
            }
        }
        isValidInternal.update { result }
        return result
    }
}
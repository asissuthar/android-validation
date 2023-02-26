package com.asissuthar.validation.formfields

import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import reactivecircus.flowbinding.android.widget.checkedChanges

class FormFieldCheckboxGroup(
    scope: CoroutineScope,
    private val container: View,
    private val list: List<MaterialCheckBox>,
    private val errorTextView: MaterialTextView,
    private val validation: suspend (List<String>?) -> String? = { null },
) : FormField<List<String>>() {

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

    var value: List<String>?
        get() = stateInternal.value
        set(value) {
            list.forEach { materialCheckBox -> materialCheckBox.isChecked = materialCheckBox.text.toString() in value.orEmpty() }
        }

    init {
        combine(list.map { it.checkedChanges() }) { values -> values.toList() }.onEach { values ->
            clearError()
            stateInternal.update { values.mapIndexedNotNull() { index, isSelected -> if (isSelected) list[index].text.toString() else null } }
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
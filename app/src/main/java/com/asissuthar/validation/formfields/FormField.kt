package com.asissuthar.validation.formfields

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class FormField<T> {
    protected val stateInternal = MutableStateFlow<T?>(null)

    // state is StateFlow. It will be helpful for collecting any change in current value.
    val state = stateInternal.asStateFlow()

    protected val isValidInternal = MutableStateFlow(true)

    // isValid is StateFlow. It will be helpful for collecting any change in validation process.
    val isValid = isValidInternal.asStateFlow()

    // We will call validate method, when we want to perform validation.
    abstract suspend fun validate(focusIfError: Boolean = true): Boolean

    open fun clearError() {}
    open fun clearFocus() {}
    open fun disable() {}
    open fun enable() {}
}

suspend fun Collection<FormField<*>>.validate(validateAll: Boolean = false) = coroutineScope {
    if (validateAll) {
        map { formField -> async { formField.validate(focusIfError = false) } }.awaitAll().all { result -> result }
    } else {
        all { formField -> formField.validate() }
    }
}

fun Collection<FormField<*>>.clearError() {
    forEach { formField -> formField.clearError() }
}

fun Collection<FormField<*>>.clearFocus() {
    forEach { formField -> formField.clearFocus() }
}

fun Collection<FormField<*>>.disable() {
    forEach { formField -> formField.disable() }
}

fun Collection<FormField<*>>.enable() {
    forEach { formField -> formField.enable() }
}
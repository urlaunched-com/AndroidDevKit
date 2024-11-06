package com.urlaunched.android.common.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.urlaunched.android.common.logger.NotLoggable
import com.urlaunched.android.common.response.ErrorData
import com.urlaunched.android.common.response.Response
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

suspend fun <T : Any> ViewModel.performUseCase(
    useCase: suspend () -> Response<T>,
    success: suspend (data: T) -> Unit,
    error: suspend (error: ErrorData) -> Unit
) {
    val useCaseParams = getUseCaseParams(useCase)
    val isUseCaseLoggable = isUseCaseLoggable(useCase, this)

    when (val response = useCase()) {
        is Response.Success -> {
            logFirebaseMessage(
                params = useCaseParams,
                responseData = response.data,
                isSuccess = true,
                isUseCaseLoggable = isUseCaseLoggable
            )

            success(response.data)
        }

        is Response.Error -> {
            logFirebaseMessage(
                params = useCaseParams,
                responseData = response.error.toString(),
                isSuccess = false,
                isUseCaseLoggable = isUseCaseLoggable
            )

            error(response.error)
        }
    }
}

inline fun ViewModel.loadingTask(setLoading: (isLoading: Boolean) -> Unit, block: () -> Unit) {
    try {
        setLoading(true)
        block()
    } finally {
        setLoading(false)
    }
}

private fun <T : Any> getUseCaseParams(useCase: T): String {
    val useCaseClass = useCase::class.java
    return useCaseClass.declaredFields
        .filterNot { it.name in setOf("label", "this$0") }
        .joinToString { field ->
            field.isAccessible = true
            val value = field.get(useCase)
            "${field.name.removePrefix("$")} = $value"
        }
}

private fun <T : Any, R : Any> isUseCaseLoggable(useCase: T, viewModel: R): Boolean {
    val currentMethodName = useCase::class.java.declaredFields.first().toString()
    val startIndex = currentMethodName.indexOf('$') + 1
    val endIndex = currentMethodName.indexOf('$', startIndex)

    val currentMethodNameFormated = currentMethodName.substring(startIndex, endIndex)
    val viewModelMethods = viewModel::class.declaredFunctions

    val currentMethod = viewModelMethods.first { it.name == currentMethodNameFormated }

    return currentMethod.findAnnotation<NotLoggable>() == null
}

private fun logFirebaseMessage(params: String, responseData: Any, isSuccess: Boolean, isUseCaseLoggable: Boolean) {
    if (isUseCaseLoggable) {
        val data = if (isSuccess) {
            when {
                responseData is Unit -> SUCCESS_MESSAGE.format(UNIT)
                (responseData as? List<*>)?.isEmpty() == true -> SUCCESS_MESSAGE.format(EMPTY_LIST)
                else -> SUCCESS_MESSAGE.format(responseData.toString())
            }
        } else {
            ERROR_MESSAGE.format(responseData)
        }

        val fireBaseMessage = FIREBASE_MESSAGE.format(params, data)

        Firebase.crashlytics.log(fireBaseMessage)
    }
}

const val FIREBASE_MESSAGE = "performUseCase(%s) %s"
const val SUCCESS_MESSAGE = "Success: %s"
const val ERROR_MESSAGE = "Error: %s"
const val UNIT = "Unit"
const val EMPTY_LIST = "Empty list"
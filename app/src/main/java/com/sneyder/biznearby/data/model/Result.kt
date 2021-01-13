package com.sneyder.biznearby.data.model

data class Result<out T>(
    val success: T? = null,
    val error: Throwable? = null
) {
    val isLoading: Boolean
        get() = success == null && error == null
}

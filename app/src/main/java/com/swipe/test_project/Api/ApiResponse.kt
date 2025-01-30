package com.swipe.test_project.Api
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}

package com.example.network.model

sealed interface NetworkResponse<out T> {
    data class Success<T>(val data: T): NetworkResponse<T>
    data class Failed<T>(val data: T, val message: String = ""): NetworkResponse<T>
    data class Error(val message: String, val exception: Exception? = null): NetworkResponse<Nothing>
}

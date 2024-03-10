package com.example.network.model

data class Result<T>(
    var data: T? = null,
    val message: String = "",
    val version: Long = System.currentTimeMillis()
)

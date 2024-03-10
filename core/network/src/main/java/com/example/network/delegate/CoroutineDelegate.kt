package com.example.network.delegate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface CoroutineDelegate {

    fun runCoroutine(context: CoroutineContext = Dispatchers.IO, block: suspend CoroutineScope.() -> Unit): Job
}
package com.example.network.delegate.impl

import com.example.network.delegate.CoroutineDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CoroutineDelegateImpl : CoroutineDelegate {
    override fun runCoroutine(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ): Job = CoroutineScope(context = context).launch(block = block)
}
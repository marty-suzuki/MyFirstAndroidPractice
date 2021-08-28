package com.martysuzuki.viewmodel.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.TestCoroutineScope

@ExperimentalCoroutinesApi
class SuspendSpy<Res, Params>(
    private val nullResultMessage: String
) {
    var result: Res? = null
    var params: Params? = null
        private set
    var calledCount: Int = 0
        private set

    private val coroutineScope = TestCoroutineScope().apply {
        pauseDispatcher()
    }

    fun clear() {
        result = null
        params = null
        calledCount = 0
    }

    fun advanceTimeBy(delayTimeMillis: Long) = coroutineScope.advanceTimeBy(delayTimeMillis)

    suspend fun respond(params: Params): Res {
        calledCount += 1
        this.params = params
        return coroutineScope.async {
            return@async result ?: throw IllegalArgumentException(nullResultMessage)
        }.await()
    }
}
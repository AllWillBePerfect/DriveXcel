package org.my.drivexcel.v4.base.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class IOUseCase<T, R>(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : UseCase<T, R> {

    final override fun execute(input: T, onResult: (R) -> Unit) {
        scope.launch {
            val result = withContext(Dispatchers.IO) {executeInBackground(input)}
            onResult(result)
        }
    }

    abstract fun executeInBackground(request: T): R
}
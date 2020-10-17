package me.danetnaverno.editoni.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.danetnaverno.editoni.editor.MainThreadContext
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object MainThreadScope : CoroutineScope
{
    override val coroutineContext: CoroutineContext
        get() = MainThreadContext
}

object ChunkBakingScope : CoroutineScope
{
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default
}

object ChunkReadingScope : CoroutineScope
{
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default
}

suspend inline fun <T> CompletableFuture<T>.await(): T
{
    return suspendCoroutine { continuation ->
        this.handle { result, exception ->
            if (result != null)
                continuation.resume(result)
            else
                continuation.resumeWithException(exception!!)
        }
    }
}
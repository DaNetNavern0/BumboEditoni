package me.danetnaverno.editoni.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

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

object ChunkDataProcessingScope : CoroutineScope
{
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default
}

//todo maybe all 3 coroutine scopes would like to have less threads, and the io scope would like to have its own thread pool
object FileReadingScope : CoroutineScope
{
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}
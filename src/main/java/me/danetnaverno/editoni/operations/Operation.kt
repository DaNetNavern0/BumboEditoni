package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.world.Chunk


abstract class Operation
{
    lateinit var operationList: OperationList
        internal set
    abstract val displayName: String
    abstract val alteredChunks: Collection<Chunk>

    abstract fun apply()
    abstract fun rollback()
}

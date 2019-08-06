package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.Chunk

abstract class Operation
{
    abstract val displayName: String
    abstract val alteredChunks: Collection<Chunk>

    abstract fun apply()
    abstract fun rollback()
}

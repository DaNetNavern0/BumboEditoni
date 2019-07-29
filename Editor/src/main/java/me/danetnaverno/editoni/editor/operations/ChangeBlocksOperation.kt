package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.editor.Editor

abstract class ChangeBlocksOperation(protected var blocks: Collection<Block>) : Operation()
{
    protected var rollback = blocks.mapNotNull { it -> it.chunk.getBlockAt(it.location) }.toList()

    override fun apply()
    {
        innerApply(blocks)
    }

    override fun rollback()
    {
        innerApply(rollback)
    }

    protected open fun innerApply(blocks: Collection<Block>)
    {
        for (block in blocks)
            Editor.currentWorld.setBlock(block)
    }
}

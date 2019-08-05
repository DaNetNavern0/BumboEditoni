package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.util.Translation

class SetBlocksOperation(protected val blocks: Collection<Block>) : Operation()
{
    protected lateinit var replacedBlocks : Collection<Block>

    override fun apply()
    {
        val world = blocks.first().chunk.world
        replacedBlocks = blocks.mapNotNull { it.chunk.world.getBlockAt(it.location) }.toList()
        for (block in blocks)
            world.setBlock(block)
    }

    override fun rollback()
    {
        val world = blocks.first().chunk.world
        for (oldBlock in replacedBlocks)
            world.setBlock(oldBlock)
    }

    override fun getDisplayName(): String
    {
        val min = blocks.minBy { it.location.localX + it.location.localY + it.location.localZ }
        val max = blocks.maxBy { it.location.localX + it.location.localY + it.location.localZ }
        return Translation.translate("operation.set", min?.location, max?.location)
    }
}
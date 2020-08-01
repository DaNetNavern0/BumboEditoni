package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.util.location.BlockArea
import me.danetnaverno.editoni.world.Block

open class SetBlocksOperation(area: BlockArea, protected val blocks: Collection<Block>) : Operation()
{
    override val displayName = Translation.translate("operation.set",
            blocks.minByOrNull { it.location.localX + it.location.localY + it.location.localZ },
            blocks.maxByOrNull { it.location.localX + it.location.localY + it.location.localZ })

    override val alteredChunks = area.toChunkArea()

    lateinit var replacedBlocks: Collection<Block>

    override fun initialApply()
    {
        replacedBlocks = blocks.mapNotNull { it.chunk.world.getLoadedBlockAt(it.location) }.toList()
        reapply()
    }

    override fun reapply()
    {
        val world = blocks.first().chunk.world
        for (block in blocks)
            world.setBlock(block)
    }

    override fun rollback()
    {
        val world = blocks.first().chunk.world
        for (oldBlock in replacedBlocks)
            world.setBlock(oldBlock)
    }
}
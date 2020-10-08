package me.danetnaverno.editoni.operation

import me.danetnaverno.editoni.location.BlockArea
import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.world.Block

open class SetBlocksOperation(area: BlockArea, protected val blocks: Collection<Block>) : Operation()
{
    override val displayName = Translation.translate("operation.set",
            blocks.minByOrNull { it.blockLocation.localX + it.blockLocation.localY + it.blockLocation.localZ },
            blocks.maxByOrNull { it.blockLocation.localX + it.blockLocation.localY + it.blockLocation.localZ })

    override val alteredChunks = area.toChunkArea()

    lateinit var replacedBlocks: Collection<Block>

    override fun initialApply()
    {
        replacedBlocks = blocks.mapNotNull { it.chunk.world.getBlockAt(it.blockLocation) }
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
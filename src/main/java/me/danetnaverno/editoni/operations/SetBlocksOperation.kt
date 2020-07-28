package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.world.Block
import me.danetnaverno.editoni.world.Chunk

open class SetBlocksOperation(protected val blocks: Collection<Block>) : Operation()
{
    override val displayName = Translation.translate("operation.set",
            blocks.minByOrNull { it.location.localX + it.location.localY + it.location.localZ },
            blocks.maxByOrNull { it.location.localX + it.location.localY + it.location.localZ })

    override val alteredChunks: Collection<Chunk> = blocks.map { it.chunk }.toSet()

    lateinit var replacedBlocks: Collection<Block>

    override fun apply()
    {
        val world = blocks.first().chunk.world
        replacedBlocks = blocks.mapNotNull { it.chunk.world.getLoadedBlockAt(it.location) }.toList()
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
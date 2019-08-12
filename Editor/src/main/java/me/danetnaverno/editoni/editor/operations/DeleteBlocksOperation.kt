package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.editor.BlockArea
import me.danetnaverno.editoni.util.Translation

open class DeleteBlocksOperation(protected val area: BlockArea) : Operation()
{
    override val displayName = Translation.translate("operation.delete", area.min, area.max)
    override val alteredChunks: Collection<Chunk> = area.mapNotNull { area.world.getChunk(it.toChunkLocation()) }.toSet()

    protected lateinit var deletedBlocks: Collection<Block>

    override fun apply()
    {
        deletedBlocks = area.mapNotNull { area.world.getLoadedBlockAt(it) }
        for (location in area)
            area.world.deleteBlock(location)
    }

    override fun rollback()
    {
        for (deletedBlock in deletedBlocks)
            area.world.setBlock(deletedBlock)
    }
}
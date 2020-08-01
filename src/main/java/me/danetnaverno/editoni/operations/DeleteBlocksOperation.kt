package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.util.location.BlockArea
import me.danetnaverno.editoni.world.Block

open class DeleteBlocksOperation(protected val area: BlockArea) : Operation()
{
    override val displayName = Translation.translate("operation.delete", area.min, area.max)
    //override val alteredChunks = area.mutableIterator().asSequence().mapNotNull { area.world.getChunk(it.toChunkLocation()) }.toSet() //todo BlockArea -> ChunkArea
    override val alteredChunks = area.toChunkArea()

    private lateinit var deletedBlocks: Collection<Block>

    override fun initialApply()
    {
        deletedBlocks = area.mutableIterator().asSequence().mapNotNull { area.world.getLoadedBlockAt(it) }.toList()
        reapply()
    }

    override fun reapply() //todo there is the difference betwee applyFirst and applyToRollback
    {
        for (location in area.mutableIterator())
            area.world.deleteBlock(location)
    }

    override fun rollback()
    {
        for (deletedBlock in deletedBlocks)
            area.world.setBlock(deletedBlock)
    }
}
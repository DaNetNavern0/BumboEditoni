package me.danetnaverno.editoni.operation

import me.danetnaverno.editoni.location.BlockArea
import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.world.Block

open class DeleteBlocksOperation(protected val area: BlockArea) : Operation()
{
    override val displayName = Translation.translate("operation.delete", area.min, area.max)
    override val alteredChunks = area.toChunkArea()

    private lateinit var deletedBlocks: Collection<Block>

    override fun initialApply()
    {
        deletedBlocks = area.mutableIterator().asSequence().mapNotNull { area.world.getBlockAt(it) }.toList()
        reapply()
    }

    override fun reapply()
    {
        for (blockLocation in area.mutableIterator())
            area.world.deleteBlock(blockLocation)
    }

    override fun rollback()
    {
        for (deletedBlock in deletedBlocks)
            area.world.setBlock(deletedBlock)
    }
}
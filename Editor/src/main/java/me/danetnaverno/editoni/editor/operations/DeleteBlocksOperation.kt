package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.editor.BlockArea
import me.danetnaverno.editoni.util.Translation

class DeleteBlocksOperation(protected val area: BlockArea) : Operation()
{
    protected lateinit var deletedBlocks : Collection<Block>

    override fun apply()
    {
        deletedBlocks = area.mapNotNull { area.world.getBlockAt(it) }.toList()
        for (location in area)
            area.world.deleteBlock(location)
    }

    override fun rollback()
    {
        for (deletedBlock in deletedBlocks)
            area.world.setBlock(deletedBlock)
    }

    override fun getDisplayName(): String
    {
        return Translation.translate("operation.delete", area.min, area.max)
    }
}
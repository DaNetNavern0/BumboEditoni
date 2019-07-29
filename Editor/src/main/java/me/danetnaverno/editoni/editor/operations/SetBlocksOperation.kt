package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.util.Translation

class SetBlocksOperation(blocks: Collection<Block>) : ChangeBlocksOperation(blocks)
{
    override fun getDisplayName(): String
    {
        val min = blocks.minBy { it.location.localX + it.location.localY + it.location.localZ }
        val max = blocks.maxBy { it.location.localX + it.location.localY + it.location.localZ }
        return Translation.translate("operation.set", min?.location, max?.location)
    }
}

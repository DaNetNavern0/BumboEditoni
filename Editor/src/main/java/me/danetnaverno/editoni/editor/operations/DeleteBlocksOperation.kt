package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.util.Translation

class DeleteBlocksOperation : ChangeBlocksOperation
{
    constructor(blocks: Collection<Block>) : super(blocks.map {
        Block(it.chunk, it.location, it.chunk.world.airType, null, null)
    })

    override fun getDisplayName(): String
    {
        val min = blocks.minBy { it.location.localX + it.location.localY + it.location.localZ }
        val max = blocks.maxBy { it.location.localX + it.location.localY + it.location.localZ }
        return Translation.translate("operation.delete", min?.location, max?.location)
    }

    override fun innerApply(blocks: Collection<Block>)
    {
        for (block in blocks)
            Editor.currentWorld.setBlock(block)
    }
}
package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.util.Translation

class PasteBlocksOperation(blocks: Collection<Block>) : SetBlocksOperation(blocks)
{
    override val displayName: String

    init
    {
        val min = blocks.minBy { it.location.localX + it.location.localY + it.location.localZ }
        val max = blocks.maxBy { it.location.localX + it.location.localY + it.location.localZ }
        displayName = Translation.translate("operation.paste", min?.location, max?.location)
    }
}
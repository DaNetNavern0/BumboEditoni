package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.world.Block

class PasteBlocksOperation(blocks: Collection<Block>) : SetBlocksOperation(blocks)
{
    override val displayName = Translation.translate("operation.paste",
            blocks.minByOrNull { it.location.localX + it.location.localY + it.location.localZ },
            blocks.maxByOrNull { it.location.localX + it.location.localY + it.location.localZ })
}
package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.util.location.BlockArea
import me.danetnaverno.editoni.world.Block

class PasteBlocksOperation(area: BlockArea, blocks: Collection<Block>) : SetBlocksOperation(area, blocks)
{
    override val displayName = Translation.translate("operation.paste",
            blocks.minByOrNull { it.location.localX + it.location.localY + it.location.localZ },
            blocks.maxByOrNull { it.location.localX + it.location.localY + it.location.localZ })
}
package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.location.BlockArea
import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.world.Block

/**
 * Paste Blocks is essentially Set Blocks, but with its own locale
 *   and that supposed to be called with blocks from a Clipboard in the future
 */
class PasteBlocksOperation(area: BlockArea, blocks: Collection<Block>) : SetBlocksOperation(area, blocks)
{
    override val displayName = Translation.translate("operation.paste",
            blocks.minByOrNull { it.location.localX + it.location.localY + it.location.localZ },
            blocks.maxByOrNull { it.location.localX + it.location.localY + it.location.localZ })
}
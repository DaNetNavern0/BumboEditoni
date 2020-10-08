package me.danetnaverno.editoni.operation

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
            blocks.minByOrNull { it.blockLocation.localX + it.blockLocation.localY + it.blockLocation.localZ },
            blocks.maxByOrNull { it.blockLocation.localX + it.blockLocation.localY + it.blockLocation.localZ })
}
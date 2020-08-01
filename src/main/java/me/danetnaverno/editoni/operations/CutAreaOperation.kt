package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.clipboard.ClipboardPrototype
import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.util.location.BlockArea

class CutAreaOperation(area: BlockArea) : DeleteBlocksOperation(area)
{
    override val displayName = Translation.translate("operation.cut", area.min, area.max)

    override fun initialApply()
    {
        area.mutableIterator().forEach { ClipboardPrototype.add(area.world.getLoadedBlockAt(it)) }
        super.initialApply()
    }
}

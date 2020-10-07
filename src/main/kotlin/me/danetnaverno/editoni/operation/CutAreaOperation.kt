package me.danetnaverno.editoni.operation

import me.danetnaverno.editoni.clipboard.ClipboardPrototype
import me.danetnaverno.editoni.location.BlockArea
import me.danetnaverno.editoni.util.Translation

class CutAreaOperation(area: BlockArea) : DeleteBlocksOperation(area)
{
    override val displayName = Translation.translate("operation.cut", area.min, area.max)

    override fun initialApply()
    {
        area.mutableIterator().asSequence().mapNotNull { area.world.getBlockAt(it) }.forEach { ClipboardPrototype.add(it) }
        super.initialApply()
    }
}

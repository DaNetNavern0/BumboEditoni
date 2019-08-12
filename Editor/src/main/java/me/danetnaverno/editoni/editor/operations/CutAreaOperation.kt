package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.editor.BlockArea
import me.danetnaverno.editoni.editor.clipboard.ClipboardPrototype
import me.danetnaverno.editoni.util.Translation

class CutAreaOperation(area: BlockArea) : DeleteBlocksOperation(area)
{
    override val displayName = Translation.translate("operation.cut", area.min, area.max)

    override fun apply()
    {
        ClipboardPrototype.add(area.getLocations().mapNotNull { area.world.getLoadedBlockAt(it) })
        super.apply()
    }
}

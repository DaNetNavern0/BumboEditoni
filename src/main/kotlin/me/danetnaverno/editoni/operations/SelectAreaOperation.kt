package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.location.BlockArea
import me.danetnaverno.editoni.util.Translation

class SelectAreaOperation(val area: BlockArea?) : Operation(), IObservingOperation
{
    override val displayName: String =
            if (area == null)
                Translation.translate("operation.deselect")
            else
                Translation.translate("operation.select", area.min, area.max)
    override val alteredChunks = area?.toChunkArea()

    private var previousArea: BlockArea? = null

    override fun initialApply()
    {
        val operations = world.editorTab.operationList.all
        var lastSelect : SelectAreaOperation? = null
        for (operation in operations)
        {
            if (operation == this)
                break
            else if (operation is SelectAreaOperation)
                lastSelect = operation
        }
        previousArea = lastSelect?.area
        reapply()
    }

    override fun reapply()
    {
        world.editorTab.selectArea(area)
    }

    override fun rollback()
    {
        world.editorTab.selectArea(previousArea)
    }
}

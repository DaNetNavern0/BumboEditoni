package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.editor.BlockArea
import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.world.Chunk

class SelectAreaOperation(val area: BlockArea?) : Operation()
{
    override val displayName: String =
            if (area == null)
                Translation.translate("operation.deselect")
            else
                Translation.translate("operation.select", area.min, area.max)
    override val alteredChunks: Collection<Chunk> =
            if (area != null)
                area.mapNotNull { area.world.getChunk(it.toChunkLocation()) }.toSet()
            else
                emptySet()
    private var previousArea: BlockArea? = null

    override fun apply()
    {
        val operations = operationList.all
        var lastSelect : SelectAreaOperation? = null
        for (operation in operations)
        {
            if (operation == this)
                break
            else if (operation is SelectAreaOperation)
                lastSelect = operation
        }
        previousArea = lastSelect?.area
        operationList.editorTab.selectArea(area)
    }

    override fun rollback()
    {
        operationList.editorTab.selectArea(previousArea)
    }
}

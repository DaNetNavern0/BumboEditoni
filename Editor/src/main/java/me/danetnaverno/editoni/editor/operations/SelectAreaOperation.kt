package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.editor.BlockArea
import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.util.Translation

class SelectAreaOperation(protected var area: BlockArea?) : Operation()
{
    override val displayName: String = Translation.translate("operation.set", area?.min, area?.max)
    override val alteredChunks: Collection<Chunk> =
            if (area != null) area!!.mapNotNull { area!!.world.getChunk(it.toChunkLocation()) }.toSet() else emptySet()

    override fun apply()
    {
        Editor.selectArea(area)
    }

    override fun rollback()
    {
        Editor.selectArea(null)
    }
}

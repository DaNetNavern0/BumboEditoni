package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.world.Chunk
import me.danetnaverno.editoni.world.Entity

class SelectEntityOperation(private val entity: Entity?) : Operation()
{
    override val displayName = Translation.translate("operation.select", entity)
    override val alteredChunks: Collection<Chunk> =
            if (entity != null)
                listOf(entity.chunk)
            else
                emptyList()

    override fun apply()
    {
        operationList.editorTab.selectEntity(entity)
    }

    override fun rollback()
    {
        operationList.editorTab.selectEntity(null)
    }
}

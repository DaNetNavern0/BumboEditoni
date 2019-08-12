package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.util.Translation

class SelectEntityOperation(protected var entity: Entity) : Operation()
{
    override val displayName = Translation.translate("operation.select", entity)
    override val alteredChunks: Collection<Chunk> = listOf(entity.chunk)

    override fun apply()
    {
        Editor.selectEntity(entity)
    }

    override fun rollback()
    {
    }
}

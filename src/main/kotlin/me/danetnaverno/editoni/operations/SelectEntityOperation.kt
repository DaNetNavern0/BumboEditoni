package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.world.Entity

class SelectEntityOperation(private val entity: Entity?) : Operation(), IObservingOperation
{
    override val displayName = Translation.translate("operation.select", entity)
    override val alteredChunks = null //todo

    override fun initialApply()
    {
        reapply()
    }

    override fun reapply()
    {
        world.editorTab.selectEntity(entity)
    }

    override fun rollback()
    {
        world.editorTab.selectEntity(null)
    }
}

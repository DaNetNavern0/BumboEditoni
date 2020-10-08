package me.danetnaverno.editoni.operation

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
        editorTab.selectEntity(entity)
    }

    override fun rollback()
    {
        editorTab.selectEntity(null)
    }
}

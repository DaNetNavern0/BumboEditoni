package me.danetnaverno.editoni.operation

import me.danetnaverno.editoni.editor.Editor
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
        Editor.getTab(world).selectEntity(entity)
    }

    override fun rollback()
    {
        Editor.getTab(world).selectEntity(null)
    }
}
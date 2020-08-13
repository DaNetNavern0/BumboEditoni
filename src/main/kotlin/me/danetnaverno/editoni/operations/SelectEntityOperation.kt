package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.world.Entity

class SelectEntityOperation(private val entity: Entity?) : Operation()
{
    override val displayName = Translation.translate("operation.select", entity)
    override val alteredChunks = null //todo

    override fun initialApply()
    {
        reapply()
    }

    override fun reapply()
    {
        operationList.editorTab.selectEntity(entity)
    }

    override fun rollback()
    {
        operationList.editorTab.selectEntity(null)
    }
}

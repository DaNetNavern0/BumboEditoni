package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.util.Translation

class SelectEntityOperation(protected var entity: Entity) : Operation()
{
    override fun apply()
    {
        Editor.selectEntity(entity)
    }

    override fun rollback()
    {
    }

    override fun getDisplayName(): String
    {
        return Translation.translate("operation.set", entity)
    }
}

package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.util.Translation

open class SaveOperation() : Operation()
{
    override fun apply()
    {
    }

    override fun rollback()
    {
    }

    override fun getDisplayName(): String
    {
        return Translation.translate("operation.save")
    }
}

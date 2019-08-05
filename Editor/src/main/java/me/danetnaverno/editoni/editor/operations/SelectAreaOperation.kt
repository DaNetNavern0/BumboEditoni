package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.editor.BlockArea
import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.util.Translation

class SelectAreaOperation(protected var area: BlockArea?) : Operation()
{
    override fun apply()
    {
        Editor.selectArea(area)
    }

    override fun rollback()
    {
        Editor.selectArea(null)
    }

    override fun getDisplayName(): String
    {
        return Translation.translate("operation.set", area?.min, area?.max)
    }
}

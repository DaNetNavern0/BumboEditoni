package me.danetnaverno.editoni.editor.operations

import me.danetnaverno.editoni.common.world.io.WorldIO
import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.util.Translation
import java.nio.file.Paths

open class SaveOperation : Operation()
{
    override fun apply()
    {
        WorldIO.writeWorld(Editor.currentWorld, Paths.get("data/output"))
        Editor.logger.info("Saved!")
    }

    override fun rollback()
    {
    }

    override fun getDisplayName(): String
    {
        return Translation.translate("operation.save")
    }
}

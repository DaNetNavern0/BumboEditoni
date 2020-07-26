package me.danetnaverno.editoni

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.editor.EditorApplication
import me.danetnaverno.editoni.editor.EditorGUI
import java.nio.file.Paths

object Main
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        EditorApplication.main(args) {
            MinecraftDictionaryFiller.init()
            Editor.currentWorld = Editor.loadWorld(Paths.get("tests/1.14.2 survival world/region"))
            Editor.currentWorld.getRegions().forEach { it.loadAllChunks() }
            Editor.placeholder()
            //EditorGUI.refreshWorldList()
        }
    }
}
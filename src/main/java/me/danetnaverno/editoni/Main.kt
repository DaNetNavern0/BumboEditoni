package me.danetnaverno.editoni

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.editor.EditorApplication
import me.danetnaverno.editoni.editor.EditorGUI
import java.nio.file.Paths
import kotlin.system.exitProcess

object Main
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        EditorApplication.main(args) {
            MinecraftDictionaryFiller.init()
            //Editor.currentWorld = Editor.loadWorld(Paths.get("tests/1.14.2 survival world/region"))
            Editor.loadWorlds(Paths.get("tests/1.14.2 world/region")).forEach { Editor.createNewTab(it) }
            Editor.openTab(Editor.tabs.values.first())
            EditorGUI.refreshWorldList()
        }
    }

    fun terminate()
    {
        exitProcess(1)
    }
}
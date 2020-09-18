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
            val world = Editor.loadWorld(Paths.get("tests/1.14.2 survival world/region"))
            val tab = Editor.createNewTab(world)
            Editor.openTab(tab)
            EditorGUI.refreshWorldList()
        }
    }

    fun terminate()
    {
        exitProcess(1)
    }
}
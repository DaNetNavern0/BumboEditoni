package me.danetnaverno.editoni

import me.danetnaverno.editoni.editor.EditorApplication
import kotlin.system.exitProcess

object Main
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        EditorApplication.launch(args)
    }

    fun terminate()
    {
        exitProcess(1)
    }
}
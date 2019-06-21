package me.danetnaverno.editoni

import me.danetnaverno.editoni.editor.EditorApplication

object Main
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        EditorApplication.main(args)
    }

    @JvmStatic
    fun displayLoop()
    {
        try
        {
            Prototype.displayLoop()
        }
        catch (e: Throwable)
        {
            e.printStackTrace()
        }
    }
}
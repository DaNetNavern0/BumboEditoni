package me.danetnaverno.editoni

import me.danetnaverno.editoni.render.EditorApplication
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object MainProcess
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        EditorApplication.main(args)
    }

    @JvmStatic
    fun initMainLoop()
    {
        Prototype.init()
        val scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleAtFixedRate({ mainLoop() }, 100, 100, TimeUnit.MILLISECONDS)
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

    @JvmStatic
    fun mainLoop()
    {
        Prototype.mainLoop()
    }
}
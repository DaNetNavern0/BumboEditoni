package me.danetnaverno.editoni

import me.danetnaverno.editoni.render.LWJGLWindow
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object MainProcess
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        LWJGLWindow().run()
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
package me.danetnaverno.editoni.editor

import lwjgui.LWJGUIUtil
import lwjgui.glfw.ClientSync
import lwjgui.scene.Window
import lwjgui.scene.WindowManager
import lwjgui.scene.WindowThread
import org.lwjgl.glfw.GLFW

/**
 * By default, LWJGUI's (don't confuse with LWJGL) loop syncs at 120 fps, which means it's busy waiting quite a lot, and making the entire app load 10%.
 * This hack make it consume only 2% (Without LWJGUI app consumes less than 1%)
 * todo - this isn't nice
 */
abstract class LWJGUIApplicationPatched
{
    abstract fun start(args: Array<String>, window: Window)

    private fun loop()
    {
        val sync = ClientSync()
        while (!WindowManager.isEmpty())
        {
            WindowManager.update()
            sync.sync(20)
        }
    }

    private fun dispose()
    {
        WindowManager.dispose()
    }

    companion object
    {
        fun launch(program: LWJGUIApplicationPatched, args: Array<String>)
        {
            if (LWJGUIUtil.restartJVMOnFirstThread(true, program.javaClass, *args))
                return

            check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
            WindowManager.init()
            val thread: WindowThread = object : WindowThread(100, 100, "lwjgui", false)
            {
                override fun init(window: Window)
                {
                    super.init(window)
                    program.start(args, window)
                }
            }
            thread.start()

            program.loop()
            program.dispose()

            GLFW.glfwTerminate()
        }
    }
}
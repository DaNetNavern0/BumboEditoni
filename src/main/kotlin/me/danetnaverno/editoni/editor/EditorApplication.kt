package me.danetnaverno.editoni.editor

import lwjgui.LWJGUIApplication
import lwjgui.scene.Context
import lwjgui.scene.Scene
import lwjgui.scene.Window
import me.danetnaverno.editoni.editor.Editor.displayLoop
import me.danetnaverno.editoni.editor.EditorGUI.init
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL44.*
import org.lwjgl.util.vector.Matrix4f
import kotlin.math.tan

class EditorApplication : LWJGUIApplication()
{
    override fun start(args: Array<String>, window: Window)
    {
        //todo inspect lwjgui for the excessive creation of short-living StyleOperation-s
        windowId = window.context.window.id
        window.setTitle("Bumbo Editoni")
        window.scene = Scene(init(), WIDTH.toDouble(), HEIGHT.toDouble())
        val vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())!!
        GLFW.glfwSwapInterval(1)
        GLFW.glfwSetWindowPos(windowId, (vidMode.width() - WIDTH) / 2, (vidMode.height() - HEIGHT) / 2)
        window.show()
        window.isWindowAutoClear = false
        window.setRenderingCallback(Renderer())
        doAfterStart.run()
    }

    internal class Renderer : lwjgui.gl.Renderer
    {
        private var frameStamp = System.currentTimeMillis()
        override fun render(context: Context, width: Int, height: Int)
        {
            try
            {
                glClearColor(0.8f, 0.9f, 1.0f, 1.0f)
                glViewport(PANEL_WIDTH.toInt(), 0, width - PANEL_WIDTH.toInt() * 2, height)
                glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
                glEnable(GL_TEXTURE_2D)
                glEnable(GL_BLEND)
                glEnable(GL_DEPTH_TEST)
                glEnable(GL_CULL_FACE)
                glCullFace(GL_NONE)
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

                projectionMatrix = Matrix4f()

                val fov = 90f
                val aspect = (width - PANEL_WIDTH.toFloat() * 2) / height.toFloat()
                val zNear = 0.01f
                val zFar = 1000f
                val scaleY = 1f / tan(Math.toRadians(fov / 2.0)).toFloat()
                val scaleX = scaleY / aspect
                val zLength = zFar - zNear

                projectionMatrix.m00 = scaleX
                projectionMatrix.m11 = scaleY
                projectionMatrix.m22 = -((zFar + zNear) / zLength)
                projectionMatrix.m23 = -1f
                projectionMatrix.m32 = -(2 * zNear * zFar / zLength)
                projectionMatrix.m33 = 0f

                displayLoop()
                val deltaTime = System.currentTimeMillis() - frameStamp
                fps = (1000f / deltaTime).toInt()
                frameStamp = System.currentTimeMillis()
            }
            catch (e: Throwable)
            {
                e.printStackTrace()
            }
        }
    }

    companion object
    {
        const val WIDTH = 1500
        const val HEIGHT = 768
        const val PANEL_WIDTH = 250.0
        var fps = 0
        var windowId: Long = 0
            private set

        private lateinit var doAfterStart: Runnable
        lateinit var projectionMatrix: Matrix4f //todo move this to a right place

        fun main(args: Array<String>, doAfterStart: Runnable)
        {
            ModernOpenGL = false
            this.doAfterStart = doAfterStart
            launch(EditorApplication(), args)
        }
    }
}
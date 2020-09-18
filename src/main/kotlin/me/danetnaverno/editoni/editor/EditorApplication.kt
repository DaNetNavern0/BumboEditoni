package me.danetnaverno.editoni.editor

import lwjgui.scene.Context
import lwjgui.scene.Scene
import lwjgui.scene.Window
import me.danetnaverno.editoni.util.ThreadExecutor
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL44.*
import kotlin.math.tan


class EditorApplication : LWJGUIApplicationPatched()
{
    override fun start(args: Array<String>, window: Window)
    {
        //todo inspect lwjgui for the excessive creation of short-living StyleOperation-s
        windowId = window.context.window.id

        window.setTitle("Bumbo Editoni")
        window.scene = Scene(EditorGUI.init(), WIDTH.toDouble(), HEIGHT.toDouble())
        val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!
        glfwSwapInterval(1)
        glfwSetWindowPos(windowId, (vidMode.width() - WIDTH) / 2, (vidMode.height() - HEIGHT) / 2)
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

                val fov = 90f
                val aspect = (width - PANEL_WIDTH.toFloat() * 2) / height.toFloat()
                val zNear = 0.01f
                val zFar = 1000f
                val scaleY = 1f / tan(Math.toRadians(fov / 2.0)).toFloat()
                val scaleX = scaleY / aspect
                val zLength = zFar - zNear

                combinedMatrix = Matrix4f()

                combinedMatrix.m00(scaleX)
                combinedMatrix.m11(scaleY)
                combinedMatrix.m22(-((zFar + zNear) / zLength))
                combinedMatrix.m23(-1f)
                combinedMatrix.m32(-(2 * zNear * zFar / zLength))
                combinedMatrix.m33(0f)

                combinedMatrix.rotate(Math.toRadians(Editor.currentTab.camera.pitch).toFloat(), Vector3f(-1f, 0f, 0f))
                combinedMatrix.rotate(Math.toRadians(Editor.currentTab.camera.yaw).toFloat(), Vector3f(0f, -1f, 0f))
                combinedMatrix.translate(Vector3f(-Editor.currentTab.camera.x.toFloat(), -Editor.currentTab.camera.y.toFloat(), -Editor.currentTab.camera.z.toFloat()))

                Editor.displayLoop()
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
        lateinit var combinedMatrix: Matrix4f //todo move this to a right place
        val mainThreadExecutor = ThreadExecutor() //todo move this to a right place

        fun main(args: Array<String>, doAfterStart: Runnable)
        {
            this.doAfterStart = doAfterStart
            launch(EditorApplication(), args)
        }
    }
}
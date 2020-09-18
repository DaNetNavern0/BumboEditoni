package me.danetnaverno.editoni.editor

import lwjgui.scene.Context
import lwjgui.scene.Scene
import lwjgui.scene.Window
import me.danetnaverno.editoni.util.ThreadExecutor
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL33.*
import java.awt.Rectangle
import kotlin.math.tan

object EditorApplication : LWJGUIApplicationPatched()
{
    private const val WIDTH = 1500
    private const val HEIGHT = 768
    private const val SIDE_PANEL_WIDTH = 250
    private const val MENU_BAR_HEIGHT = 24

    val mainThreadExecutor = ThreadExecutor() //todo move this to a right place

    var fps = 0
    var windowId: Long = 0
        private set
    lateinit var combinedMatrix: Matrix4f //todo move this to a right place

    val windowWidth
        get() = window.width
    val windowHeight
        get() = window.height
    val viewportDimensions
        get() = Rectangle(SIDE_PANEL_WIDTH, MENU_BAR_HEIGHT, window.width - SIDE_PANEL_WIDTH * 2, window.height - MENU_BAR_HEIGHT)
    val sidePanelWidth
        get() = SIDE_PANEL_WIDTH
    val menuBarHeight
        get() = MENU_BAR_HEIGHT

    private lateinit var doAfterStart: Runnable
    private lateinit var window: Window

    fun main(args: Array<String>, doAfterStart: Runnable)
    {
        this.doAfterStart = doAfterStart
        launch(this, args)
    }

    override fun start(args: Array<String>, window: Window)
    {
        //todo inspect lwjgui for the excessive creation of short-living StyleOperation-s
        windowId = window.context.window.id
        this.window = window

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
                glViewport(SIDE_PANEL_WIDTH, 0, width - SIDE_PANEL_WIDTH * 2, height - MENU_BAR_HEIGHT)
                glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
                glEnable(GL_BLEND)
                glEnable(GL_DEPTH_TEST)
                glEnable(GL_CULL_FACE)
                glCullFace(GL_NONE)
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

                val fov = 90f
                val aspect = (width - SIDE_PANEL_WIDTH.toFloat() * 2) / height.toFloat()
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

                Editor.displayLoop()
                val deltaTime = System.currentTimeMillis() - frameStamp
                fps = (1000f / deltaTime).toInt()
                frameStamp = System.currentTimeMillis()
            }
            catch (e: Throwable)
            {
                Editor.logger.error(e)
            }
        }
    }
}
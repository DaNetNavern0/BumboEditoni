package me.danetnaverno.editoni.editor

import kotlinx.coroutines.CoroutineDispatcher
import lwjgui.gl.Renderer
import lwjgui.scene.Context
import lwjgui.scene.Scene
import lwjgui.scene.Window
import me.danetnaverno.editoni.MinecraftDictionaryFiller
import me.danetnaverno.editoni.editor.raw.LWJGUIApplicationPatched
import me.danetnaverno.editoni.editor.raw.RawInputHandler
import me.danetnaverno.editoni.render.Shader
import me.danetnaverno.editoni.texture.TextureAtlas
import me.danetnaverno.editoni.world.ChunkManager
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL33.*
import java.awt.Rectangle
import java.nio.file.Paths
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext
import kotlin.math.tan

/**
 * This class represents the backbones of the Editor - starting an app, rendering loop etc.
 * For editor-related operations look at [Editor]
 */
object EditorApplication : LWJGUIApplicationPatched(), Renderer
{
    private const val WIDTH = 1500
    private const val HEIGHT = 768
    private const val SIDE_PANEL_WIDTH = 250
    private const val MENU_BAR_HEIGHT = 24

    var fps = 0
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

    private lateinit var window: Window
    private var frameStamp = System.currentTimeMillis()
    private val mainThreadTasks = ConcurrentLinkedQueue<Runnable>()

    fun launch(args: Array<String>)
    {
        launch(this, args)
    }

    override fun start(args: Array<String>, window: Window)
    {
        //todo inspect lwjgui for the excessive creation of short-living StyleOperation-s
        check(!this::window.isInitialized) { "EditorApplication had already been initialized" }

        Thread.currentThread().name = "Main"
        this.window = window
        val windowId = window.context.window.id
        window.setTitle("Bumbo Editoni")
        window.scene = Scene(EditorGUI.initialize(), WIDTH.toDouble(), HEIGHT.toDouble())
        val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!
        glfwSwapInterval(1)
        glfwSetWindowPos(windowId, (vidMode.width() - WIDTH) / 2, (vidMode.height() - HEIGHT) / 2)
        window.show()
        window.isWindowAutoClear = false

        window.setRenderingCallback(this)
        RawInputHandler.initialize(windowId)

        MinecraftDictionaryFiller.initialize()
        Editor.switchTab(Editor.loadWorldIntoTab(Paths.get("tests/1.14.2 survival world/region")))
        EditorGUI.refreshWorldList()
    }

    override fun render(context: Context, width: Int, height: Int)
    {
        try
        {
            setUpProjection()
            tickCoroutineContext()
            tickGeneral()
            tickBaking()
            tickDisplay()

            val deltaTime = System.currentTimeMillis() - frameStamp
            fps = (1000f / deltaTime).toInt()
            frameStamp = System.currentTimeMillis()
        }
        catch (e: Throwable)
        {
            Editor.logger.error("Error!", e)
        }
    }

    private fun setUpProjection()
    {
        val fov = 90f
        val aspect = (windowWidth - sidePanelWidth.toFloat() * 2) / windowHeight.toFloat()
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
    }

    private fun tickCoroutineContext()
    {
        while (true)
        {
            val task = mainThreadTasks.poll() ?: break
            task.run()
        }
    }

    private fun tickGeneral()
    {
        ChunkManager.loadChunksInLoadingDistance(Editor.currentWorld, Editor.currentTab.camera.mutableLocation.toChunkLocation())
    }

    private fun tickBaking()
    {
        Editor.currentWorld.worldRenderer.tickBaking(Editor.currentTab.camera.mutableLocation.toChunkLocation())
    }

    private fun tickDisplay()
    {
        glClearColor(0.8f, 0.9f, 1.0f, 1.0f)
        glViewport(
                sidePanelWidth,
                0,
                windowWidth - sidePanelWidth * 2,
                windowHeight - menuBarHeight)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glEnable(GL_BLEND)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_NONE)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        Shader.use()
        TextureAtlas.mainAtlas.bind()
        Editor.currentWorld.worldRenderer.render()

        EditorUserInputHandler.controls()
        EditorUserInputHandler.selections()
        RawInputHandler.update()
    }

    object MainThreadContext : CoroutineDispatcher()
    {
        override fun dispatch(context: CoroutineContext, block: Runnable)
        {
            mainThreadTasks.add(block)
        }
    }
}
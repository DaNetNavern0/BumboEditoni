package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.io.IMinecraftWorldIO
import me.danetnaverno.editoni.world.World
import org.apache.logging.log4j.LogManager
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import java.nio.file.Path
import kotlin.math.abs

/**
 * This class represents editor-related actions (opening tabs, raycasting etc)
 * For the editor's core at [EditorApplication]
 */
object Editor
{
    val logger = LogManager.getLogger("Editor")!!

    val editorTabs : List<EditorTab> = arrayListOf()
    lateinit var currentTab: EditorTab
        private set
    val currentWorld: World
        get() = currentTab.world


    fun loadWorldIntoTab(worldPath: Path): EditorTab
    {
        val world = IMinecraftWorldIO.getWorldIO(worldPath).openWorld(worldPath)
        val editorTab = EditorTab(world)
        (editorTabs as MutableList<EditorTab>).add(editorTab)
        return editorTab
    }

    fun closeTabAndSwitch(tabToClose: EditorTab, tabToOpen: EditorTab)
    {
        (editorTabs as MutableList<EditorTab>).remove(tabToClose)
        switchTab(tabToOpen)
    }

    fun switchTab(tab: EditorTab)
    {
        currentTab = tab
    }

    fun getTab(world: World) : EditorTab
    {
        return editorTabs.first { it.world == world }
    }

    fun raycast(screenX: Int, screenY: Int): Vector3f?
    {
        val visibleViewport = EditorApplication.viewportDimensions
        MemoryStack.stackPush().use { stack ->
            val buf = stack.mallocFloat(1)
            val reverseY = EditorApplication.windowHeight - screenY
            glReadPixels(screenX, reverseY, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, buf)
            val screenZ = buf.get(0)
            return unProject(screenX.toFloat(), reverseY.toFloat(), screenZ,
                    EditorApplication.combinedMatrix,
                    visibleViewport.x.toFloat(), 0f,
                    visibleViewport.width.toFloat(), visibleViewport.height.toFloat())
        }
    }

    private fun unProject(screenX: Float, screenY: Float, screenZ: Float,
                          combinedMatrix: Matrix4f,
                          viewportOffsetX: Float, viewportOffsetY: Float, viewportWidth: Float, viewportHeight: Float): Vector3f?
    {
        val inVector = Vector4f()
        val inv = Matrix4f()
        combinedMatrix.invert(inv)

        inVector.x = ((screenX - viewportOffsetX) / viewportWidth) * 2.0f - 1.0f
        inVector.y = ((screenY - viewportOffsetY) / viewportHeight) * 2.0f - 1.0f
        inVector.z = screenZ * 2.0f - 1.0f
        inVector.w = 1.0f

        val outVector = inv.transform(inVector)
        if (abs(outVector.w) < 0.0001)
            return null
        outVector.w = 1.0f / outVector.w
        return Vector3f(outVector.x * outVector.w, outVector.y * outVector.w, outVector.z * outVector.w)
    }
}
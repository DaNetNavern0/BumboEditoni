package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.io.Minecraft114WorldIO
import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.location.EntityLocation
import me.danetnaverno.editoni.world.Block
import me.danetnaverno.editoni.world.Entity
import me.danetnaverno.editoni.world.World
import org.apache.logging.log4j.LogManager
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Vector4f
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack
import java.nio.file.Path
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/**
 * This class represents editor-related actions (opening tabs, raycasting etc)
 * For the editor's core at [EditorApplication]
 */
object Editor
{
    val logger = LogManager.getLogger("Editor")!!

    val worlds = arrayListOf<World>()
    lateinit var currentTab: EditorTab
        private set

    fun loadWorldIntoTab(worldPath: Path): World
    {
        val world = Minecraft114WorldIO().readWorld(worldPath)
        worlds.add(world)
        return world
    }

    fun unloadWorld(world: World)
    {
        worlds.remove(world)
    }

    fun openTab(tab: EditorTab)
    {
        currentTab = tab
    }

    fun findEntity(world: World, location: EntityLocation): Entity?
    {
        return world.getEntitiesAt(location.add(0.0, -0.5, 0.0), 1f)?.firstOrNull()
    }

    fun findBlock(world: World, point: Vector3f): Block?
    {
        if (point.y < 0 || point.y > 255)
            return null
        val floor = Vector3i(floor(point.x).toInt() - 1, floor(point.y).toInt() - 1, floor(point.z).toInt() - 1)
        val ceiling = Vector3i(ceil(point.x).toInt() + 1, ceil(point.y).toInt() + 1, ceil(point.z).toInt() + 1)

        var closest: Block? = null
        var min = Double.MAX_VALUE

        for (x in floor.x..ceiling.x)
            for (y in floor.y..ceiling.y)
                for (z in floor.z..ceiling.z)
                {
                    val dx = point.x - (x + 0.5)
                    val dy = point.y - (y + 0.5)
                    val dz = point.z - (z + 0.5)
                    val distance = dx * dx + dy * dy + dz * dz
                    if (distance < min)
                    {
                        val block = world.getLoadedBlockAt(BlockLocation(x, y, z))
                        if (block != null && !block.type.isHidden /*&& !hiddenBlocks.contains(block.location)*/)
                        {
                            closest = block
                            min = distance
                        }
                    }
                }
        return closest
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

    /*fun selectEntity(entity: Entity?)
    {
        selectedArea = null
        selectedEntity = entity
        EditorUserHandler.selectedCorner = null
        EditorGUI.refreshSelectInfoLabel()
    }

    fun selectArea(area: BlockArea?)
    {
        selectedEntity = null
        selectedArea = area
        EditorUserHandler.selectedCorner = null
        EditorGUI.refreshSelectInfoLabel()
    }

    fun getHiddenBlocks(): List<BlockLocation>
    {
        return hiddenBlocks.toList()
    }*/
}
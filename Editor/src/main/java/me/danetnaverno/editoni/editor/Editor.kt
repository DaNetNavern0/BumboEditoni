package me.danetnaverno.editoni.editor

import com.jogamp.opengl.glu.GLU
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.common.world.io.WorldIO
import me.danetnaverno.editoni.util.Camera
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.EntityLocation
import org.apache.logging.log4j.LogManager
import org.joml.Vector3d
import org.joml.Vector3i
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import java.nio.file.Path

object Editor : AbstractEditor()
{
    val logger = LogManager.getLogger("Editor")!!

    var _currentWorld: World? = null

    override var currentWorld: World
        get() = _currentWorld!!
        set(value)
        {
            _currentWorld = value
            EditorGUI.refreshWorldList()
        }

    init
    {
        InputHandler.init(EditorApplication.getWindowId())
        try
        {
            Camera.x = 0.0
            Camera.y = 80.0
            Camera.z = 0.0
            Camera.yaw = 0.0
            Camera.pitch = -20.0
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun loadWorld(worldPath: Path) : World
    {
        val loadedWorld = worlds[worldPath]
        if (loadedWorld == null)
        {
            val loadedWorlds = WorldIO.readWorlds(worldPath)
            if (loadedWorlds.isEmpty())
                return currentWorld
            for (world in loadedWorlds)
                worlds[world.path] = world
            return loadedWorlds.first()
        }
        return loadedWorld
    }

    fun displayLoop()
    {
        GL11.glRotated(Camera.pitch, -1.0, 0.0, 0.0)
        GL11.glRotated(Camera.yaw, 0.0, -1.0, 0.0)
        GL11.glTranslated(-Camera.x, -Camera.y, -Camera.z)

        currentWorld.worldRenderer.render()

        EditorUserHandler.controls()
        EditorUserHandler.selections()
    }

    fun findEntity(world: World, location: EntityLocation): Entity?
    {
        return world.getEntitiesAt(location.add(0.0, -0.5, 0.0), 1f).firstOrNull()
    }

    fun findBlock(world: World, point: Vector3d): Block?
    {
        val floor = Vector3i(Math.floor(point.x).toInt() - 1, Math.floor(point.y).toInt() - 1, Math.floor(point.z).toInt() - 1)
        val ceiling = Vector3i(Math.ceil(point.x).toInt() + 1, Math.ceil(point.y).toInt() + 1, Math.ceil(point.z).toInt() + 1)

        var closest: Block? = null
        var min = Double.MAX_VALUE

        for (x in floor.x..ceiling.x)
            for (y in floor.y..ceiling.y)
                for (z in floor.z..ceiling.z)
                {
                    val distance = point.distanceSquared(x + 0.5, y + 0.5, z + 0.5)
                    if (distance < min)
                    {
                        val block = world.getBlockAt(BlockLocation(x, y, z))
                        if (block != null && !isHidden(block))
                        {
                            closest = block
                            min = distance
                        }
                    }
                }
        return closest
    }

    fun raycast(screenX: Int, screenY: Int): Vector3d
    {
        val viewport = BufferUtils.createIntBuffer(4)
        val mvmatrix = BufferUtils.createFloatBuffer(16)
        val projmatrix = BufferUtils.createFloatBuffer(16)
        val output = BufferUtils.createFloatBuffer(4)

        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, mvmatrix)
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projmatrix)
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport)

        val reverseY = viewport.get(3) - screenY
        val winZ = BufferUtils.createFloatBuffer(1)
        GL11.glReadPixels(screenX, reverseY, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, winZ)
        val z = winZ.get(0)
        GLU().gluUnProject(screenX.toFloat(), reverseY.toFloat(), z, mvmatrix, projmatrix, viewport, output)
        return Vector3d(output.get(0).toDouble(), output.get(1).toDouble(), output.get(2).toDouble())
    }

    fun selectEntity(entity: Entity?)
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
    }

    private fun isHidden(block: Block): Boolean
    {
        return block.type.id.path.contains("air") || hiddenBlocks.contains(block.location) //todo magic value
    }
}
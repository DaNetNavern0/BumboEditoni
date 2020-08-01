package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.blockrender.Shader
import me.danetnaverno.editoni.io.Minecraft114WorldIO
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.EntityLocation
import me.danetnaverno.editoni.world.Block
import me.danetnaverno.editoni.world.Entity
import me.danetnaverno.editoni.world.World
import org.apache.logging.log4j.LogManager
import org.joml.Vector3d
import org.joml.Vector3i
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL44.*
import org.lwjgl.util.glu.GLU
import java.nio.file.Path
import kotlin.math.ceil
import kotlin.math.floor

object Editor
{
    val logger = LogManager.getLogger("Editor")!!
    var renderDistance = 10

    lateinit var currentTab: EditorTab
        private set
    val tabs = mutableMapOf<Path, EditorTab>()

    init
    {
        InputHandler.init(EditorApplication.getWindowId())
    }

    fun loadWorlds(worldPath: Path): Collection<World>
    {
        return Minecraft114WorldIO().readWorlds(worldPath)
    }

    fun createNewTab(world: World): EditorTab
    {
        val tab = EditorTab(world)
        tabs[world.path] = tab
        return tab
    }

    fun openTab(tab: EditorTab)
    {
        currentTab = tab
    }

    fun displayLoop()
    {
        glRotated(currentTab.camera.pitch, -1.0, 0.0, 0.0)
        glRotated(currentTab.camera.yaw, 0.0, -1.0, 0.0)
        glTranslated(-currentTab.camera.x, -currentTab.camera.y, -currentTab.camera.z)

        Shader.bind()
        currentTab.worldRenderer.bake()
        currentTab.worldRenderer.render()

        EditorUserHandler.selections()
        EditorUserHandler.controls()
        InputHandler.update()
    }

    fun findEntity(world: World, location: EntityLocation): Entity?
    {
        return world.getEntitiesAt(location.add(0.0, -0.5, 0.0), 1f)?.firstOrNull()
    }

    fun findBlock(world: World, point: Vector3d): Block?
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
                    val distance = point.distanceSquared(x + 0.5, y + 0.5, z + 0.5)
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

    fun raycast(screenX: Int, screenY: Int): Vector3d
    {
        val viewport = BufferUtils.createIntBuffer(4)
        val mvmatrix = BufferUtils.createFloatBuffer(16)
        val projmatrix = BufferUtils.createFloatBuffer(16)
        val output = BufferUtils.createFloatBuffer(4)

        glGetFloatv(GL_MODELVIEW_MATRIX, mvmatrix)
        glGetFloatv(GL_PROJECTION_MATRIX, projmatrix)
        glGetIntegerv(GL_VIEWPORT, viewport)

        val reverseY = viewport.get(3) - screenY
        val winZ = BufferUtils.createFloatBuffer(1)
        glReadPixels(screenX, reverseY, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, winZ)
        val z = winZ.get(0)
        //Yes, GLU is dated, but it does the job and it's not like we run it so often its performance matter
        GLU.gluUnProject(screenX.toFloat(), reverseY.toFloat(), z, mvmatrix, projmatrix, viewport, output)
        return Vector3d(output.get(0).toDouble(), output.get(1).toDouble(), output.get(2).toDouble())
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
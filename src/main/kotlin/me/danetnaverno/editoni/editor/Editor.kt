package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.io.Minecraft114WorldIO
import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.location.EntityLocation
import me.danetnaverno.editoni.render.Shader
import me.danetnaverno.editoni.texture.TextureAtlas
import me.danetnaverno.editoni.world.Block
import me.danetnaverno.editoni.world.Entity
import me.danetnaverno.editoni.world.World
import org.apache.logging.log4j.LogManager
import org.joml.Vector3i
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL44.*
import org.lwjgl.util.glu.GLU
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
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
        InputHandler.init(EditorApplication.windowId)
    }

    fun unloadWorld(world: World)
    {
        tabs.remove(world.path)
    }

    fun loadWorld(worldPath: Path): World
    {
        return Minecraft114WorldIO().readWorld(worldPath)
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
        Shader.use()
        glBindTexture(GL_TEXTURE_2D_ARRAY, TextureAtlas.mainAtlas.atlasTexture)

        currentTab.worldRenderer.bake()
        currentTab.worldRenderer.render()

        EditorUserHandler.controls()
        EditorUserHandler.selections()
        InputHandler.update()
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

    fun raycast(screenX: Int, screenY: Int): Vector3f
    {
        //todo Yes, this isn't particularly nice and quite buggy, and uses GLU which is heavily dated, but it will work for now
        val viewport = BufferUtils.createIntBuffer(4)
        val mvmatrix = BufferUtils.createFloatBuffer(16)
        val projmatrix = BufferUtils.createFloatBuffer(16)
        val output = BufferUtils.createFloatBuffer(4)

        val idMatrix = Matrix4f()
        mvmatrix.put(idMatrix.m00)
        mvmatrix.put(idMatrix.m01)
        mvmatrix.put(idMatrix.m01)
        mvmatrix.put(idMatrix.m03)
        mvmatrix.put(idMatrix.m10)
        mvmatrix.put(idMatrix.m11)
        mvmatrix.put(idMatrix.m12)
        mvmatrix.put(idMatrix.m13)
        mvmatrix.put(idMatrix.m20)
        mvmatrix.put(idMatrix.m21)
        mvmatrix.put(idMatrix.m22)
        mvmatrix.put(idMatrix.m23)
        mvmatrix.put(idMatrix.m30)
        mvmatrix.put(idMatrix.m31)
        mvmatrix.put(idMatrix.m32)
        mvmatrix.put(idMatrix.m33)

        val projMatrix = EditorApplication.combinedMatrix
        projmatrix.put(projMatrix.m00)
        projmatrix.put(projMatrix.m01)
        projmatrix.put(projMatrix.m01)
        projmatrix.put(projMatrix.m03)
        projmatrix.put(projMatrix.m10)
        projmatrix.put(projMatrix.m11)
        projmatrix.put(projMatrix.m12)
        projmatrix.put(projMatrix.m13)
        projmatrix.put(projMatrix.m20)
        projmatrix.put(projMatrix.m21)
        projmatrix.put(projMatrix.m22)
        projmatrix.put(projMatrix.m23)
        projmatrix.put(projMatrix.m30)
        projmatrix.put(projMatrix.m31)
        projmatrix.put(projMatrix.m32)
        projmatrix.put(projMatrix.m33)

        mvmatrix.rewind()
        projmatrix.rewind()
        glGetIntegerv(GL_VIEWPORT, viewport)

        val reverseY = viewport.get(3) - screenY
        val winZ = BufferUtils.createFloatBuffer(1)
        glReadPixels(screenX, reverseY, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, winZ)
        val z = winZ.get(0)
        GLU.gluUnProject(screenX.toFloat(), reverseY.toFloat(), z, mvmatrix, projmatrix, viewport, output)
        return Vector3f(output.get(0), output.get(1), output.get(2))
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
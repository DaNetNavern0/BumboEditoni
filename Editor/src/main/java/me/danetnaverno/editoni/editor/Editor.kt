package me.danetnaverno.editoni.editor

import com.jogamp.opengl.glu.GLU
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.BlockRendererCube
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.common.world.io.WorldIO
import me.danetnaverno.editoni.editor.operations.DeleteBlocksOperation
import me.danetnaverno.editoni.editor.operations.Operations
import me.danetnaverno.editoni.texture.Texture
import me.danetnaverno.editoni.util.Camera
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.EntityLocation
import org.apache.logging.log4j.LogManager
import org.joml.Vector3d
import org.joml.Vector3i
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.nio.file.Path
import java.nio.file.Paths


object Editor : AbstractEditor()
{
    val logger = LogManager.getLogger("Editor")!!

    init
    {
        InputHandler.init(EditorApplication.getWindowId())
        try
        {
            Camera.x = 0f
            Camera.y = 80f
            Camera.z = 0f
            Camera.yaw = 0f
            Camera.pitch = -20f
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun loadWorld(worldPath: Path) : World
    {
        if (!worlds.containsKey(worldPath))
        {
            val world = WorldIO.readWorld(worldPath)
            worlds[worldPath] = world
            return world
        }
        else
        {
            throw NotImplementedError() //todo
        }
    }

    fun displayLoop()
    {
        GL11.glRotated(Camera.pitch.toDouble(), -1.0, 0.0, 0.0)
        GL11.glRotated(Camera.yaw.toDouble(), 0.0, -1.0, 0.0)
        GL11.glTranslated(-Camera.x.toDouble(), -Camera.y.toDouble(), -Camera.z.toDouble())

        val block = Editor.selectedBlock
        if (block != null)
        {
            BlockRendererCube(Texture[ResourceLocation("common:select")]).draw(block.chunk.world, block.location)
        }
        Editor.currentWorld.worldRenderer.render()

        controls()
    }

    fun controls()
    {
        if (InputHandler.keyDown(GLFW.GLFW_KEY_A))
            Camera.x -= 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_D))
            Camera.x += 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_W))
            Camera.z -= 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_S))
            Camera.z += 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
            Camera.y -= 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_SPACE))
            Camera.y += 0.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_1))
            Camera.yaw += 4.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_2))
            Camera.yaw -= 4.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_3))
            Camera.pitch += 4.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_4))
            Camera.pitch -= 4.2f
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_Z)
                && (InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || InputHandler.keyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)))
        {
            if (InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || InputHandler.keyDown(GLFW.GLFW_KEY_RIGHT_SHIFT))
                Operations.moveForward()
            else
                Operations.moveBack()
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_H))
        {
            if (InputHandler.keyDown(GLFW.GLFW_KEY_RIGHT_SHIFT) || InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
                hiddenBlocks.clear()
            else
            {
                val block = selectedBlock
                if (block != null)
                    hiddenBlocks.add(block.location)
            }
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_DELETE))
        {
            val block = selectedBlock
            if (block != null)
            {
                val operation = DeleteBlocksOperation(listOf(block))
                Operations.apply(operation)
            }
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_S) && InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
        {
            WorldIO.writeWorld(currentWorld, Paths.get("data/output"))
            logger.info("Saved!")
        }

        InputHandler.update()
    }

    fun onMouseClick(x: Int, y: Int)
    {
        val raycast = raycast(x, y)
        val entity = findEntity(EntityLocation(raycast.x, raycast.y, raycast.z))
        if (entity != null)
            selectEntity(entity)
        else
            selectBlock(findBlock(raycast(x, y)))
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

    fun getHiddenBlocks(): List<BlockLocation>
    {
        return hiddenBlocks.toList()
    }

    private fun findEntity(location: EntityLocation): Entity?
    {
        return currentWorld.getEntitiesAt(location.add(0.0, -0.5, 0.0), 1f).firstOrNull()
    }

    private fun findBlock(point: Vector3d): Block?
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
                        val block = currentWorld.getBlockAt(BlockLocation(x, y, z))
                        if (block != null && !isHidden(block))
                        {
                            closest = block
                            min = distance
                        }
                    }
                }
        return closest
    }

    private fun selectEntity(entity: Entity)
    {
        selectedBlock = null
        selectedEntity = entity
        EditorGUI.refreshBlockInfoLabel()
    }

    fun selectBlock(block: Block?)
    {
        selectedEntity = null
        selectedBlock = block
        EditorGUI.refreshBlockInfoLabel()
    }

    private fun isHidden(block: Block): Boolean
    {
        return block.type.id.path.contains("air") || hiddenBlocks.contains(block.location) //todo magic value
    }
}
package me.danetnaverno.editoni.editor

import com.jogamp.opengl.glu.GLU
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.common.world.io.WorldIO
import me.danetnaverno.editoni.editor.operations.*
import me.danetnaverno.editoni.texture.Texture
import me.danetnaverno.editoni.util.Camera
import me.danetnaverno.editoni.util.Translation
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.EntityLocation
import org.apache.logging.log4j.LogManager
import org.joml.Vector3d
import org.joml.Vector3i
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.nio.file.Path
import javax.swing.JOptionPane


object Editor : AbstractEditor()
{
    val logger = LogManager.getLogger("Editor")!!

    var selectedCorner: BlockLocation? = null

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
        if (Operations.getOperations().last() !is SaveOperation)
        {
            val lastSave = Operations.getOperations().indexOfLast { it is SaveOperation }
            val dialogButton = JOptionPane.showConfirmDialog(null,
                    Translation.translate("operation.confirm_unsaved", Operations.getOperations().size - lastSave), "", JOptionPane.YES_NO_CANCEL_OPTION)
            if (dialogButton == JOptionPane.YES_OPTION)
            {
                Operations.setPosition(Operations.getOperations().size - 1)
                Operations.apply(SaveOperation())
            }
            else if (dialogButton == JOptionPane.CANCEL_OPTION)
                return currentWorld
        }
        val loadedWorld = worlds[worldPath]
        if (loadedWorld == null)
        {
            val world = WorldIO.readWorld(worldPath)
            worlds[worldPath] = world
            return world
        }
        return loadedWorld
    }

    fun displayLoop()
    {
        GL11.glRotated(Camera.pitch, -1.0, 0.0, 0.0)
        GL11.glRotated(Camera.yaw, 0.0, -1.0, 0.0)
        GL11.glTranslated(-Camera.x, -Camera.y, -Camera.z)

        currentWorld.worldRenderer.render()

        controls()

        val area = selectedArea
        if (area != null)
            renderSelection(area)
        val corner = selectedCorner
        if (corner != null)
        {
            val mouse = InputHandler.getMouseCoords()
            val secondCorner = findBlock(raycast(mouse.key.toInt(), mouse.value.toInt()))?.location
            if (secondCorner != null)
                renderSelection(BlockArea(currentWorld, corner, secondCorner))
        }
    }

    private fun renderSelection(area: BlockArea)
    {
        Texture[ResourceLocation("common:select")].bind()

        val min = EntityLocation(area.min.globalX - 0.01, area.min.globalY - 0.01, area.min.globalZ - 0.01)
        val max = EntityLocation(area.max.globalX + 1.01, area.max.globalY + 1.01, area.max.globalZ + 1.01)

        //GL11.glDisable(GL11.GL_CULL_FACE)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex3d(max.globalX, min.globalY, min.globalZ)
        GL11.glVertex3d(max.globalX, min.globalY, max.globalZ)
        GL11.glVertex3d(min.globalX, min.globalY, max.globalZ)
        GL11.glVertex3d(min.globalX, min.globalY, min.globalZ)

        GL11.glVertex3d(min.globalX, max.globalY, min.globalZ)
        GL11.glVertex3d(min.globalX, max.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, min.globalZ)

        GL11.glVertex3d(min.globalX, min.globalY, min.globalZ)
        GL11.glVertex3d(min.globalX, min.globalY, max.globalZ)
        GL11.glVertex3d(min.globalX, max.globalY, max.globalZ)
        GL11.glVertex3d(min.globalX, max.globalY, min.globalZ)

        GL11.glVertex3d(min.globalX, max.globalY, min.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, min.globalZ)
        GL11.glVertex3d(max.globalX, min.globalY, min.globalZ)
        GL11.glVertex3d(min.globalX, min.globalY, min.globalZ)

        GL11.glVertex3d(min.globalX, min.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, min.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, max.globalZ)
        GL11.glVertex3d(min.globalX, max.globalY, max.globalZ)

        GL11.glVertex3d(max.globalX, max.globalY, min.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, min.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, min.globalY, min.globalZ)

        GL11.glEnd()
    }

    fun controls()
    {
        if (InputHandler.keyDown(GLFW.GLFW_KEY_W))
        {
            Camera.x -= 20.0 / EditorApplication.fps * Math.sin(Math.toRadians(Camera.yaw)) * Math.cos(Math.toRadians(Camera.pitch))
            Camera.y += 20.0 / EditorApplication.fps * Math.sin(Math.toRadians(Camera.pitch))
            Camera.z -= 20.0 / EditorApplication.fps * Math.cos(Math.toRadians(Camera.yaw)) * Math.cos(Math.toRadians(Camera.pitch))
        }
        if (InputHandler.keyDown(GLFW.GLFW_KEY_S))
        {
            Camera.x += 20.0 / EditorApplication.fps * Math.sin(Math.toRadians(Camera.yaw)) * Math.cos(Math.toRadians(Camera.pitch))
            Camera.y -= 20.0 / EditorApplication.fps * Math.sin(Math.toRadians(Camera.pitch))
            Camera.z += 20.0 / EditorApplication.fps * Math.cos(Math.toRadians(Camera.yaw)) * Math.cos(Math.toRadians(Camera.pitch))
        }
        if (InputHandler.keyDown(GLFW.GLFW_KEY_A))
        {
            Camera.x -= 20.0 / EditorApplication.fps * Math.cos(Math.toRadians(Camera.yaw))
            Camera.z += 20.0 / EditorApplication.fps * Math.sin(Math.toRadians(Camera.yaw))
        }
        if (InputHandler.keyDown(GLFW.GLFW_KEY_D))
        {
            Camera.x += 20.0 / EditorApplication.fps * Math.cos(Math.toRadians(Camera.yaw))
            Camera.z -= 20.0 / EditorApplication.fps * Math.sin(Math.toRadians(Camera.yaw))
        }
        if (InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
            Camera.y -= 20.0 / EditorApplication.fps
        if (InputHandler.keyDown(GLFW.GLFW_KEY_SPACE))
            Camera.y += 20.0 / EditorApplication.fps
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
                val area = selectedArea
                if (area != null)
                    hiddenBlocks.addAll(area)
            }
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_DELETE))
        {
            val area = selectedArea
            if (area != null)
            {
                val operation = DeleteBlocksOperation(area)
                Operations.apply(operation)
            }
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_S) && InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
        {
            Operations.apply(SaveOperation())
        }

        InputHandler.update()
    }

    fun onMouseClick(x: Int, y: Int)
    {
        val raycast = raycast(x, y)
        val entity = findEntity(EntityLocation(raycast.x, raycast.y, raycast.z))
        if (entity != null)
            Operations.apply(SelectEntityOperation(entity))
        else
        {
            if (selectedCorner==null)
                selectedCorner = findBlock(raycast(x, y))?.location
            else
            {
                val secondCorner = findBlock(raycast(x, y))
                if (secondCorner != null)
                    Operations.apply(SelectAreaOperation(BlockArea(currentWorld, secondCorner.location, selectedCorner!!)))
            }
        }
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

    fun selectEntity(entity: Entity?)
    {
        selectedArea = null
        selectedEntity = entity
        selectedCorner = null
        EditorGUI.refreshSelectInfoLabel()
    }

    fun selectArea(area: BlockArea?)
    {
        selectedEntity = null
        selectedArea = area
        selectedCorner = null
        EditorGUI.refreshSelectInfoLabel()
    }

    private fun isHidden(block: Block): Boolean
    {
        return block.type.id.path.contains("air") || hiddenBlocks.contains(block.location) //todo magic value
    }
}
package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.world.io.WorldIO
import me.danetnaverno.editoni.editor.operations.DeleteBlocksOperation
import me.danetnaverno.editoni.editor.operations.Operations
import me.danetnaverno.editoni.editor.operations.SelectAreaOperation
import me.danetnaverno.editoni.editor.operations.SelectEntityOperation
import me.danetnaverno.editoni.texture.Texture
import me.danetnaverno.editoni.util.Camera
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.EntityLocation
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.nio.file.Paths

object EditorUserHandler
{
    var selectedCorner: BlockLocation? = null

    fun selections()
    {
        val corner = selectedCorner
        if (corner != null)
        {
            val mouse = InputHandler.getMouseCoords()
            val secondCorner = Editor.findBlock(Editor.currentWorld, Editor.raycast(mouse.first.toInt(), mouse.second.toInt()))?.location
            if (secondCorner != null && !InputHandler.mouseButtonDown(0))
                renderSelection(BlockArea(Editor.currentWorld, corner, secondCorner))
        }
        if (Editor.selectedArea != null)
            renderSelection(Editor.selectedArea!!)
    }

    fun controls()
    {
        val operations = Operations.get(Editor.currentWorld)
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
                operations.moveForward()
            else
                operations.moveBack()
        }
        if (InputHandler.keyPressed(GLFW.GLFW_KEY_H))
        {
            if (InputHandler.keyDown(GLFW.GLFW_KEY_RIGHT_SHIFT) || InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
                Editor.hiddenBlocks.clear()
            else
            {
                val area = Editor.selectedArea
                if (area != null)
                    Editor.hiddenBlocks.addAll(area)
            }
        }

        if (InputHandler.keyPressed(GLFW.GLFW_KEY_DELETE))
        {
            val area = Editor.selectedArea
            if (area != null)
            {
                val operation = DeleteBlocksOperation(area)
                operations.apply(operation)
            }
        }

        if (InputHandler.keyPressed(GLFW.GLFW_KEY_S) && InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
        {
            operations.savePosition = operations.position
            WorldIO.writeWorld(Editor.currentWorld, Paths.get("data/output"))
            GarbageChunkCollector.unloadExcessChunks(Editor.currentWorld)
            Editor.logger.info("Saved!")
        }

        if (InputHandler.keyPressed(GLFW.GLFW_KEY_ESCAPE))
        {
            selectedCorner = null
            Editor.selectArea(null)
        }

        if (InputHandler.mouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
        {
            if (Editor.selectedArea != null)
                return
            val mouseCoords = InputHandler.getMouseCoords()
            val x = mouseCoords.first.toInt()
            val y = mouseCoords.second.toInt()
            val raycast = Editor.raycast(x, y)
            val entity = Editor.findEntity(Editor.currentWorld, EntityLocation(raycast.x, raycast.y, raycast.z))
            if (entity != null)
                operations.apply(SelectEntityOperation(entity))
            else
            {
                if (selectedCorner == null)
                    selectedCorner = Editor.findBlock(Editor.currentWorld, Editor.raycast(x, y))?.location
                else
                {
                    val secondCorner = Editor.findBlock(Editor.currentWorld, Editor.raycast(x, y))
                    if (secondCorner != null)
                    {
                        operations.apply(SelectAreaOperation(BlockArea(Editor.currentWorld, secondCorner.location, selectedCorner!!)))
                        selectedCorner = null
                    }
                }
            }
        }
    }

    fun onMouseDragged(button: Int, x: Double, y: Double)
    {
        /*if (pressedMouseButton == 1)
        {
            Camera.yaw -= x - lastMousePos.first
            Camera.pitch -= y - lastMousePos.second
        }
        RobertoGarbagio.logger.info("butt:$pressedMouseButton")*/
    }

    private fun renderSelection(area: BlockArea)
    {
        Texture[ResourceLocation("common:select")].bind()

        val min = EntityLocation(area.min.globalX - 0.01, area.min.globalY - 0.01, area.min.globalZ - 0.01)
        val max = EntityLocation(area.max.globalX + 1.01, area.max.globalY + 1.01, area.max.globalZ + 1.01)

        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glLineWidth(2f)

        GL11.glBegin(GL11.GL_LINE_LOOP)
        GL11.glVertex3d(min.globalX, min.globalY, min.globalZ)
        GL11.glVertex3d(min.globalX, min.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, min.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, min.globalY, min.globalZ)
        GL11.glEnd()
        GL11.glBegin(GL11.GL_LINE_LOOP)
        GL11.glVertex3d(min.globalX, max.globalY, min.globalZ)
        GL11.glVertex3d(min.globalX, max.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, min.globalZ)
        GL11.glEnd()
        GL11.glBegin(GL11.GL_LINES)
        GL11.glVertex3d(min.globalX, min.globalY, min.globalZ)
        GL11.glVertex3d(min.globalX, max.globalY, min.globalZ)
        GL11.glVertex3d(min.globalX, min.globalY, max.globalZ)
        GL11.glVertex3d(min.globalX, max.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, min.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, max.globalZ)
        GL11.glVertex3d(max.globalX, min.globalY, min.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, min.globalZ)
        GL11.glEnd()

        GL11.glEnable(GL11.GL_DEPTH_TEST)

        GL11.glBegin(GL11.GL_QUADS)

        val size = max.substract(min)
        GL11.glTexCoord2d(size.globalX, 0.0)
        GL11.glVertex3d(max.globalX, min.globalY, min.globalZ)
        GL11.glTexCoord2d(size.globalX, size.globalZ)
        GL11.glVertex3d(max.globalX, min.globalY, max.globalZ)
        GL11.glTexCoord2d(0.0, size.globalZ)
        GL11.glVertex3d(min.globalX, min.globalY, max.globalZ)
        GL11.glTexCoord2d(0.0, 0.0)
        GL11.glVertex3d(min.globalX, min.globalY, min.globalZ)

        GL11.glTexCoord2d(0.0, 0.0)
        GL11.glVertex3d(min.globalX, max.globalY, min.globalZ)
        GL11.glTexCoord2d(0.0, size.globalZ)
        GL11.glVertex3d(min.globalX, max.globalY, max.globalZ)
        GL11.glTexCoord2d(size.globalX, size.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, max.globalZ)
        GL11.glTexCoord2d(size.globalX, 0.0)
        GL11.glVertex3d(max.globalX, max.globalY, min.globalZ)

        GL11.glTexCoord2d(0.0, 0.0)
        GL11.glVertex3d(min.globalX, min.globalY, min.globalZ)
        GL11.glTexCoord2d(0.0, size.globalZ)
        GL11.glVertex3d(min.globalX, min.globalY, max.globalZ)
        GL11.glTexCoord2d(size.globalX, size.globalZ)
        GL11.glVertex3d(min.globalX, max.globalY, max.globalZ)
        GL11.glTexCoord2d(size.globalX, 0.0)
        GL11.glVertex3d(min.globalX, max.globalY, min.globalZ)

        GL11.glTexCoord2d(0.0, size.globalZ)
        GL11.glVertex3d(min.globalX, max.globalY, min.globalZ)
        GL11.glTexCoord2d(size.globalX, size.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, min.globalZ)
        GL11.glTexCoord2d(size.globalX, 0.0)
        GL11.glVertex3d(max.globalX, min.globalY, min.globalZ)
        GL11.glTexCoord2d(0.0, 0.0)
        GL11.glVertex3d(min.globalX, min.globalY, min.globalZ)

        GL11.glTexCoord2d(0.0, 0.0)
        GL11.glVertex3d(min.globalX, min.globalY, max.globalZ)
        GL11.glTexCoord2d(size.globalX, 0.0)
        GL11.glVertex3d(max.globalX, min.globalY, max.globalZ)
        GL11.glTexCoord2d(size.globalX, size.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, max.globalZ)
        GL11.glTexCoord2d(0.0, size.globalZ)
        GL11.glVertex3d(min.globalX, max.globalY, max.globalZ)

        GL11.glTexCoord2d(size.globalX, 0.0)
        GL11.glVertex3d(max.globalX, max.globalY, min.globalZ)
        GL11.glTexCoord2d(size.globalX, size.globalZ)
        GL11.glVertex3d(max.globalX, max.globalY, max.globalZ)
        GL11.glTexCoord2d(0.0, size.globalZ)
        GL11.glVertex3d(max.globalX, min.globalY, max.globalZ)
        GL11.glTexCoord2d(0.0, 0.0)
        GL11.glVertex3d(max.globalX, min.globalY, min.globalZ)

        GL11.glEnd()
    }
}
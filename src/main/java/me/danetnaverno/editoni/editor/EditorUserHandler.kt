package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.location.BlockArea
import me.danetnaverno.editoni.location.EntityLocation
import me.danetnaverno.editoni.operations.DeleteBlocksOperation
import me.danetnaverno.editoni.operations.SelectAreaOperation
import me.danetnaverno.editoni.texture.Texture
import me.danetnaverno.editoni.texture.TextureAtlas
import me.danetnaverno.editoni.util.ResourceLocation
import me.danetnaverno.editoni.world.Block
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL44.*
import kotlin.math.cos
import kotlin.math.sin

object EditorUserHandler
{
    var selectedCorner: Block? = null

    fun selections()
    {
        val corner = selectedCorner
        if (corner != null)
        {
            val mouse = InputHandler.mouseCoords
            val secondCorner = Editor.findBlock(Editor.currentTab.world, Editor.raycast(mouse.first.toInt(), mouse.second.toInt()))?.location
            if (secondCorner != null && !InputHandler.mouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
                renderSelection(BlockArea(Editor.currentTab.world, corner.location, secondCorner))
        }
        val area = Editor.currentTab.selectedArea
        if (area != null)
            renderSelection(area)
    }

    fun controls()
    {
        val camera = Editor.currentTab.camera
        if (InputHandler.keyDown(GLFW.GLFW_KEY_W))
        {
            camera.x -= 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.yaw)) * cos(Math.toRadians(camera.pitch))
            camera.y += 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.pitch))
            camera.z -= 20.0 / EditorApplication.fps * cos(Math.toRadians(camera.yaw)) * cos(Math.toRadians(camera.pitch))
        }
        if (InputHandler.keyDown(GLFW.GLFW_KEY_S))
        {
            camera.x += 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.yaw)) * cos(Math.toRadians(camera.pitch))
            camera.y -= 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.pitch))
            camera.z += 20.0 / EditorApplication.fps * cos(Math.toRadians(camera.yaw)) * cos(Math.toRadians(camera.pitch))
        }
        if (InputHandler.keyDown(GLFW.GLFW_KEY_A))
        {
            camera.x -= 20.0 / EditorApplication.fps * cos(Math.toRadians(camera.yaw))
            camera.z += 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.yaw))
        }
        if (InputHandler.keyDown(GLFW.GLFW_KEY_D))
        {
            camera.x += 20.0 / EditorApplication.fps * cos(Math.toRadians(camera.yaw))
            camera.z -= 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.yaw))
        }
        if (InputHandler.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
            camera.y -= 20.0 / EditorApplication.fps
        if (InputHandler.keyDown(GLFW.GLFW_KEY_SPACE))
            camera.y += 20.0 / EditorApplication.fps
        if (InputHandler.keyDown(GLFW.GLFW_KEY_1))
            camera.yaw += 4.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_2))
            camera.yaw -= 4.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_3))
            camera.pitch += 4.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_4))
            camera.pitch -= 4.2f
        if (InputHandler.keyDown(GLFW.GLFW_KEY_R))
        {
            val world = Editor.currentTab.world
            Editor.unloadWorld(world)
            val newWorld = Editor.loadWorld(world.path)
            val tab = Editor.createNewTab(newWorld)
            Editor.openTab(tab)
        }

        if (InputHandler.keyReleased(GLFW.GLFW_KEY_DELETE))
        {
            val area = Editor.currentTab.selectedArea
            if (area != null)
                Editor.currentTab.operationList.apply(DeleteBlocksOperation(area))
        }

        if (InputHandler.keyReleased(GLFW.GLFW_KEY_ESCAPE))
        {
            Editor.currentTab.operationList.apply(SelectAreaOperation(null))
            Editor.currentTab.selectArea(null)
        }

        if (InputHandler.keyDown(GLFW.GLFW_KEY_5) || InputHandler.mouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
        {
            val mouseCoords = InputHandler.mouseCoords
            val x = mouseCoords.first.toInt()
            val y = mouseCoords.second.toInt()
            val raycast = Editor.raycast(x, y)
            val entity = Editor.findEntity(Editor.currentTab.world, EntityLocation(raycast.x, raycast.y, raycast.z))
            if (entity != null)
            {
            }
            else
            {
                if (selectedCorner == null)
                    selectedCorner = Editor.findBlock(Editor.currentTab.world, Editor.raycast(x, y))
                else
                {
                    val secondCorner = Editor.findBlock(Editor.currentTab.world, Editor.raycast(x, y))
                    if (secondCorner != null)
                    {
                        val area = BlockArea(Editor.currentTab.world, selectedCorner!!.location, secondCorner.location)
                        Editor.currentTab.operationList.apply(SelectAreaOperation(area))
                        Editor.currentTab.selectArea(area)
                        selectedCorner = null
                    }
                }
            }
        }
    }

    private fun renderSelection(area: BlockArea)
    {
        val tIndex = TextureAtlas.mainAtlas.getZLayer(Texture[ResourceLocation("common:select")]).toDouble()

        val min = EntityLocation(area.min.globalX - 0.01, area.min.globalY - 0.01, area.min.globalZ - 0.01)
        val max = EntityLocation(area.max.globalX + 1.01, area.max.globalY + 1.01, area.max.globalZ + 1.01)

        glDisable(GL_DEPTH_TEST)
        glLineWidth(2f)

        //todo this isn't nice
        glBegin(GL_LINE_LOOP)
        glVertex3d(min.globalX, min.globalY, min.globalZ)
        glVertex3d(min.globalX, min.globalY, max.globalZ)
        glVertex3d(max.globalX, min.globalY, max.globalZ)
        glVertex3d(max.globalX, min.globalY, min.globalZ)
        glEnd()
        glBegin(GL_LINE_LOOP)
        glVertex3d(min.globalX, max.globalY, min.globalZ)
        glVertex3d(min.globalX, max.globalY, max.globalZ)
        glVertex3d(max.globalX, max.globalY, max.globalZ)
        glVertex3d(max.globalX, max.globalY, min.globalZ)
        glEnd()
        glBegin(GL_LINES)
        glVertex3d(min.globalX, min.globalY, min.globalZ)
        glVertex3d(min.globalX, max.globalY, min.globalZ)
        glVertex3d(min.globalX, min.globalY, max.globalZ)
        glVertex3d(min.globalX, max.globalY, max.globalZ)
        glVertex3d(max.globalX, min.globalY, max.globalZ)
        glVertex3d(max.globalX, max.globalY, max.globalZ)
        glVertex3d(max.globalX, min.globalY, min.globalZ)
        glVertex3d(max.globalX, max.globalY, min.globalZ)
        glEnd()

        val size = max.subtract(min)
        glEnable(GL_DEPTH_TEST)
        glBegin(GL_QUADS)

        glTexCoord2d(size.globalX, 0.0)
        glVertex3d(max.globalX, min.globalY, min.globalZ)
        glTexCoord3d(size.globalX, size.globalZ, tIndex)
        glVertex3d(max.globalX, min.globalY, max.globalZ)
        glTexCoord3d(0.0, size.globalZ, tIndex)
        glVertex3d(min.globalX, min.globalY, max.globalZ)
        glTexCoord3d(0.0, 0.0, tIndex)
        glVertex3d(min.globalX, min.globalY, min.globalZ)

        glTexCoord3d(0.0, 0.0, tIndex)
        glVertex3d(min.globalX, max.globalY, min.globalZ)
        glTexCoord3d(0.0, size.globalZ, tIndex)
        glVertex3d(min.globalX, max.globalY, max.globalZ)
        glTexCoord3d(size.globalX, size.globalZ, tIndex)
        glVertex3d(max.globalX, max.globalY, max.globalZ)
        glTexCoord3d(size.globalX, 0.0, tIndex)
        glVertex3d(max.globalX, max.globalY, min.globalZ)

        glTexCoord3d(0.0, 0.0, tIndex)
        glVertex3d(min.globalX, min.globalY, min.globalZ)
        glTexCoord3d(0.0, size.globalZ, tIndex)
        glVertex3d(min.globalX, min.globalY, max.globalZ)
        glTexCoord3d(size.globalX, size.globalZ, tIndex)
        glVertex3d(min.globalX, max.globalY, max.globalZ)
        glTexCoord3d(size.globalX, 0.0, tIndex)
        glVertex3d(min.globalX, max.globalY, min.globalZ)

        glTexCoord3d(0.0, size.globalZ, tIndex)
        glVertex3d(min.globalX, max.globalY, min.globalZ)
        glTexCoord3d(size.globalX, size.globalZ, tIndex)
        glVertex3d(max.globalX, max.globalY, min.globalZ)
        glTexCoord3d(size.globalX, 0.0, tIndex)
        glVertex3d(max.globalX, min.globalY, min.globalZ)
        glTexCoord3d(0.0, 0.0, tIndex)
        glVertex3d(min.globalX, min.globalY, min.globalZ)

        glTexCoord3d(0.0, 0.0, tIndex)
        glVertex3d(min.globalX, min.globalY, max.globalZ)
        glTexCoord3d(size.globalX, 0.0, tIndex)
        glVertex3d(max.globalX, min.globalY, max.globalZ)
        glTexCoord3d(size.globalX, size.globalZ, tIndex)
        glVertex3d(max.globalX, max.globalY, max.globalZ)
        glTexCoord3d(0.0, size.globalZ, tIndex)
        glVertex3d(min.globalX, max.globalY, max.globalZ)

        glTexCoord3d(size.globalX, 0.0, tIndex)
        glVertex3d(max.globalX, max.globalY, min.globalZ)
        glTexCoord3d(size.globalX, size.globalZ, tIndex)
        glVertex3d(max.globalX, max.globalY, max.globalZ)
        glTexCoord3d(0.0, size.globalZ, tIndex)
        glVertex3d(max.globalX, min.globalY, max.globalZ)
        glTexCoord3d(0.0, 0.0, tIndex)
        glVertex3d(max.globalX, min.globalY, min.globalZ)

        glEnd()
    }
}
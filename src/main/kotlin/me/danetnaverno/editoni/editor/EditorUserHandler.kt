package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.location.BlockArea
import me.danetnaverno.editoni.location.EntityLocation
import me.danetnaverno.editoni.operations.DeleteBlocksOperation
import me.danetnaverno.editoni.operations.SelectAreaOperation
import me.danetnaverno.editoni.render.SelectionRenderer
import me.danetnaverno.editoni.world.Block
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin

object EditorUserHandler
{
    var selectedCorner: Block? = null
    val selectionRenderer = SelectionRenderer()

    fun selections()
    {
        val corner = selectedCorner
        if (corner != null)
        {
            val mouse = InputHandler.mouseCoords
            val raycast = Editor.raycast(mouse.first.toInt(), mouse.second.toInt()) ?: return
            val secondCorner = Editor.findBlock(Editor.currentTab.world, raycast)?.location
            if (secondCorner != null && !InputHandler.mouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
            {
                selectionRenderer.update(BlockArea(Editor.currentTab.world, corner.location, secondCorner)) //todo need to rebake the vertex data only when neccessary
                selectionRenderer.draw()
            }
        }
        val area = Editor.currentTab.selectedArea
        if (area != null)
        {
            selectionRenderer.update(area) //todo need to rebake the vertex data only when neccessary
            selectionRenderer.draw()
        }
    }

    fun controls()
    {
        val camera = Editor.currentTab.camera
        if (InputHandler.keyDown(GLFW.GLFW_KEY_W))
        {
            camera.x -= 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.yaw))
            camera.z -= 20.0 / EditorApplication.fps * cos(Math.toRadians(camera.yaw))
        }
        if (InputHandler.keyDown(GLFW.GLFW_KEY_S))
        {
            camera.x += 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.yaw))
            camera.z += 20.0 / EditorApplication.fps * cos(Math.toRadians(camera.yaw))
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
            camera.yaw += 142f / EditorApplication.fps
        if (InputHandler.keyDown(GLFW.GLFW_KEY_2))
            camera.yaw -= 142f / EditorApplication.fps
        if (InputHandler.keyDown(GLFW.GLFW_KEY_3))
            camera.pitch += 142f / EditorApplication.fps
        if (InputHandler.keyDown(GLFW.GLFW_KEY_4))
            camera.pitch -= 142f / EditorApplication.fps
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
            val raycast = Editor.raycast(x, y) ?: return
            val entity = Editor.findEntity(Editor.currentTab.world, EntityLocation(raycast.x.toDouble(), raycast.y.toDouble(), raycast.z.toDouble()))
            if (entity != null)
            {
            }
            else
            {
                if (selectedCorner == null)
                    selectedCorner = Editor.findBlock(Editor.currentTab.world, raycast)
                else
                {
                    val secondCorner = Editor.findBlock(Editor.currentTab.world, raycast)
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
}
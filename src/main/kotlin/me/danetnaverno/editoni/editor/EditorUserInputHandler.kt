package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.editor.raw.RawInputHandler
import me.danetnaverno.editoni.location.BlockArea
import me.danetnaverno.editoni.location.EntityLocation
import me.danetnaverno.editoni.operations.DeleteBlocksOperation
import me.danetnaverno.editoni.operations.SelectAreaOperation
import me.danetnaverno.editoni.render.SelectionRenderer
import me.danetnaverno.editoni.world.Block
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin

object EditorUserInputHandler
{
    var selectedCorner: Block? = null
    val selectionRenderer = SelectionRenderer()

    fun selections()
    {
        val corner = selectedCorner
        if (corner != null)
        {
            val mouse = RawInputHandler.mouseCoords
            val raycast = Editor.raycast(mouse.first.toInt(), mouse.second.toInt()) ?: return
            val secondCorner = Editor.findBlock(Editor.currentTab.world, raycast)?.location
            if (secondCorner != null && !RawInputHandler.mouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
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

    //todo This entire class is WIP. Don't mind it. It even uses the numbers keyboard row to rotate the camera %)
    fun controls()
    {
        val camera = Editor.currentTab.camera
        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_W))
        {
            camera.x -= 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.yaw))
            camera.z -= 20.0 / EditorApplication.fps * cos(Math.toRadians(camera.yaw))
        }
        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_S))
        {
            camera.x += 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.yaw))
            camera.z += 20.0 / EditorApplication.fps * cos(Math.toRadians(camera.yaw))
        }
        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_A))
        {
            camera.x -= 20.0 / EditorApplication.fps * cos(Math.toRadians(camera.yaw))
            camera.z += 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.yaw))
        }
        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_D))
        {
            camera.x += 20.0 / EditorApplication.fps * cos(Math.toRadians(camera.yaw))
            camera.z -= 20.0 / EditorApplication.fps * sin(Math.toRadians(camera.yaw))
        }
        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
            camera.y -= 20.0 / EditorApplication.fps
        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_SPACE))
            camera.y += 20.0 / EditorApplication.fps
        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_1))
            camera.yaw += 142f / EditorApplication.fps
        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_2))
            camera.yaw -= 142f / EditorApplication.fps
        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_3))
            camera.pitch += 142f / EditorApplication.fps
        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_4))
            camera.pitch -= 142f / EditorApplication.fps
        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_R))
        {
            val world = Editor.currentTab.world
            Editor.unloadWorld(world)
            Editor.openTab(Editor.loadWorldIntoTab(world.path).editorTab)
        }

        if (RawInputHandler.keyReleased(GLFW.GLFW_KEY_DELETE))
        {
            val area = Editor.currentTab.selectedArea
            if (area != null)
                Editor.currentTab.operationList.apply(DeleteBlocksOperation(area))
        }

        if (RawInputHandler.keyReleased(GLFW.GLFW_KEY_ESCAPE))
        {
            if (Editor.currentTab.selectedArea != null)
            {
                Editor.currentTab.operationList.apply(SelectAreaOperation(null))
                Editor.currentTab.selectArea(null)
            }
            selectedCorner = null
        }

        if (RawInputHandler.keyDown(GLFW.GLFW_KEY_5) || RawInputHandler.mouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
        {
            val mouseCoords = RawInputHandler.mouseCoords
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
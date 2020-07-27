package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.util.location.EntityLocation
import me.danetnaverno.editoni.world.Block
import org.lwjgl.glfw.GLFW
import kotlin.math.cos
import kotlin.math.sin

object EditorUserHandler
{
    var selectedCorner: Block? = null

    fun selections()
    {
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
                        //operations.apply(SelectAreaOperation(BlockArea(Editor.currentWorld, secondCorner.location, selectedCorner!!)))
                        Editor.currentTab.selectedArea = BlockArea(Editor.currentTab.world, selectedCorner!!.location, secondCorner.location)
                        selectedCorner = null
                    }
                }
            }
        }
    }
}
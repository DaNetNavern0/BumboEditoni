package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.util.Camera
import me.danetnaverno.editoni.util.location.BlockLocation
import org.lwjgl.glfw.GLFW

object EditorUserHandler
{
    var selectedCorner: BlockLocation? = null

    fun selections()
    {
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
    }
}
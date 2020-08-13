package me.danetnaverno.editoni.editor

import lwjgui.event.MouseEvent
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback

object InputHandler
{
    private const val KEYBOARD_SIZE = 512
    private const val MOUSE_SIZE = 16
    private const val NO_STATE = -1
    var lastMousePos = Pair(0.0, 0.0)
    private val keyStates = IntArray(KEYBOARD_SIZE)
    internal var keyboard: GLFWKeyCallback = object : GLFWKeyCallback()
    {
        override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int)
        {
            keyStates[key] = action
        }
    }
    private val mouseStates = IntArray(MOUSE_SIZE)
    private var windowId: Long = 0
    fun init(window: Long)
    {
        resetKeyboard()
        for (i in mouseStates.indices) mouseStates[i] = NO_STATE
        GLFW.glfwSetKeyCallback(window, keyboard)
        windowId = window
    }

    fun update()
    {
        lastMousePos = mouseCoords
        resetKeyboard()
        for (i in mouseStates.indices)
        {
            if (mouseStates[i] == GLFW.GLFW_PRESS) mouseStates[i] = GLFW.GLFW_REPEAT else if (mouseStates[i] == GLFW.GLFW_RELEASE) mouseStates[i] = NO_STATE
        }
        GLFW.glfwPollEvents()
    }

    private fun resetKeyboard()
    {
        for (i in keyStates.indices) if (keyStates[i] == GLFW.GLFW_RELEASE) keyStates[i] = NO_STATE
    }

    fun registerMousePress(event: MouseEvent)
    {
        mouseStates[event.button] = 1
    }

    fun registerMouseRelease(event: MouseEvent)
    {
        mouseStates[event.button] = 2
    }

    fun registerMouseDrag(it: MouseEvent)
    {
        //todo move to the proper place
        if (mouseButtonDown(1))
        {
            Editor.currentTab.camera.yaw -= it.mouseX - lastMousePos.first
            Editor.currentTab.camera.pitch -= it.mouseY - lastMousePos.second
        }
    }

    fun keyDown(key: Int): Boolean
    {
        return keyStates[key] != NO_STATE
    }

    fun keyPressed(key: Int): Boolean
    {
        return keyStates[key] == GLFW.GLFW_PRESS
    }

    fun keyReleased(key: Int): Boolean
    {
        return keyStates[key] == GLFW.GLFW_RELEASE
    }

    fun mouseButtonPressed(button: Int): Boolean
    {
        return mouseStates[button] == 1
    }

    fun mouseButtonDown(button: Int): Boolean
    {
        return mouseStates[button] != NO_STATE
    }

    val mouseCoords: Pair<Double, Double>
        get()
        {
            val posX = DoubleArray(1)
            val posY = DoubleArray(1)
            GLFW.glfwGetCursorPos(windowId, posX, posY)
            return Pair(posX[0], posY[0])
        }
}
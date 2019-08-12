package me.danetnaverno.editoni.editor;

import kotlin.Pair;
import lwjgui.event.MouseEvent;
import me.danetnaverno.editoni.util.Camera;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public final class InputHandler
{
    private static final int KEYBOARD_SIZE = 512;
    private static final int MOUSE_SIZE = 16;
    private static final int NO_STATE = -1;

    private static int[] keyStates = new int[KEYBOARD_SIZE];

    private static int[] mouseStates = new int[MOUSE_SIZE];

    public static Pair<Double, Double> lastMousePos = new Pair<>(0.0, 0.0);

    private static long windowId;

    protected static GLFWKeyCallback keyboard = new GLFWKeyCallback()
    {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods)
        {
            keyStates[key] = action;
        }
    };

    public static void init(long window)
    {
        resetKeyboard();
        for (int i = 0; i < mouseStates.length; i++)
            mouseStates[i] = NO_STATE;
        glfwSetKeyCallback(window, keyboard);
        windowId = window;
    }

    public static void update()
    {
        lastMousePos = InputHandler.getMouseCoords();
        resetKeyboard();

        for (int i = 0; i < mouseStates.length; i++)
        {
            if (mouseStates[i] == GLFW_PRESS)
                mouseStates[i] = GLFW_REPEAT;
            else if (mouseStates[i] == GLFW_RELEASE)
                mouseStates[i] = NO_STATE;
        }
        glfwPollEvents();
    }

    private static void resetKeyboard()
    {
        for (int i = 0; i < keyStates.length; i++)
            if (keyStates[i] == GLFW_RELEASE)
                keyStates[i] = NO_STATE;
    }

    public static void registerMousePress(MouseEvent event)
    {
        mouseStates[event.button] = 1;
    }

    public static void registerMouseRelease(MouseEvent event)
    {
        mouseStates[event.button] = 2;
    }

    public static void registerMouseDrag(MouseEvent it)
    {
        //todo move to the proper place
        if (mouseButtonDown(1))
        {
            Camera.yaw -= it.mouseX - lastMousePos.getFirst();
            Camera.pitch -= it.mouseY - lastMousePos.getSecond();
        }
    }

    public static boolean keyDown(int key)
    {
        return keyStates[key] != NO_STATE;
    }

    public static boolean keyPressed(int key)
    {
        return keyStates[key] == GLFW_PRESS;
    }

    public static boolean keyReleased(int key)
    {
        return keyStates[key] == GLFW_RELEASE;
    }

    public static boolean mouseButtonPressed(int button)
    {
        return mouseStates[button] == 1;
    }

    public static boolean mouseButtonDown(int button)
    {
        return mouseStates[button] != NO_STATE;
    }

    public static Pair<Double,Double> getMouseCoords()
    {
        double[] posX = new double[1];
        double[] posY = new double[1];
        glfwGetCursorPos(windowId, posX, posY);
        return new Pair<>(posX[0], posY[0]);
    }
}
package me.danetnaverno.editoni.render;

import com.jogamp.opengl.glu.GLU;
import me.danetnaverno.editoni.MainProcess;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class LWJGLWindow
{
    public static long window;
    public static int width = 1280, height = 720, depth = 600;

    public void run()
    {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init()
    {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(width, height, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush())
        {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        //glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

    }

    private void loop()
    {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
        MainProcess.initMainLoop();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window))
        {
            GL11.glViewport(0, 0, width, height);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            //GL11.glEnable(GL11.GL_BLEND);
            //GL11.glFrontFace(GL11.GL_CCW);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);
            //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();

            double fovY  = 90.0;
            double zNear = 0.01;
            double zFar  = 1000.0;
            double aspect = (double)width / (double)height;
            double fH = Math.tan(fovY / 360 * Math.PI) * zNear;
            double fW = fH * aspect;
            GL11.glFrustum(-fW, fW, -fH, fH, zNear, zFar);

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();

            MainProcess.displayLoop();

            glfwSwapBuffers(window);
        }
    }

    public static Vector3f GetOGLPos(int x, int y)
    {
        IntBuffer viewport = BufferUtils.createIntBuffer(4);
        FloatBuffer mvmatrix = BufferUtils.createFloatBuffer(16);
        FloatBuffer projmatrix = BufferUtils.createFloatBuffer(16);
        FloatBuffer output = BufferUtils.createFloatBuffer(4);

        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, mvmatrix);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projmatrix);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        y = viewport.get(3) - y;
        FloatBuffer winZ = BufferUtils.createFloatBuffer(1);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, winZ);
        float z = winZ.get(0);
        new GLU().gluUnProject(x, y, z, mvmatrix, projmatrix, viewport, output);
        System.out.println(z + ", " + output.get(0) + " " + output.get(1) + " " + output.get(2));
        return new Vector3f(output.get(0), output.get(1), output.get(2));
    }
}

package me.danetnaverno.editoni.editor;

import lwjgui.LWJGUIApplication;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.opengl.GL44.*;


public class EditorApplication extends LWJGUIApplication
{
    public static final int WIDTH = 1500;
    public static final int HEIGHT = 768;
    public static final double PANEL_WIDTH = 250;

    public static int fps = 0;

    private static long handleId;
    private static Runnable doAfterStart;

    public static void main(String[] args, Runnable function)
    {
        ModernOpenGL = false;
        doAfterStart = function;
        launch(args);
    }

    public static long getWindowId()
    {
        return handleId;
    }

    @Override
    public void start(String[] args, Window window)
    {
        //todo inspect lwjgui for the excessive creation of short-living StyleOperation-s

        handleId = window.getContext().getWindow().getID();
        window.setTitle("Bumbo Editoni");
        window.setScene(new Scene(EditorGUI.INSTANCE.init(), WIDTH, HEIGHT));
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSwapInterval(1);
        GLFW.glfwSetWindowPos(handleId, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);
        window.show();
        window.setWindowAutoClear(false);
        window.setRenderingCallback(new Renderer());

        doAfterStart.run();
    }

    static class Renderer implements lwjgui.gl.Renderer
    {
        private long frameStamp = System.currentTimeMillis();

        @Override
        public void render(Context context, int width, int height)
        {
            try
            {
                glClearColor(0.8f, 0.9f, 1.0f, 1.0f);
                glViewport((int) PANEL_WIDTH, 0, width - (int) PANEL_WIDTH * 2, height);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glEnable(GL_TEXTURE_2D);
                glEnable(GL_BLEND);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_CULL_FACE);
                glCullFace(GL_NONE);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();

                double fovY = 90.0;
                double zNear = 0.01;
                double zFar = 1000.0;
                double aspect = (width - PANEL_WIDTH * 2) / (double) height;
                double fH = Math.tan(fovY / 360 * Math.PI) * zNear;
                double fW = fH * aspect;
                glFrustum(-fW, fW, -fH, fH, zNear, zFar);

                glMatrixMode(GL_MODELVIEW);
                glLoadIdentity();

                Editor.INSTANCE.displayLoop();
                long deltaTime = System.currentTimeMillis() - frameStamp;
                fps = (int) (1000f / deltaTime);
                frameStamp = System.currentTimeMillis();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
    }
}
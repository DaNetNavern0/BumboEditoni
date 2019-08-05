package me.danetnaverno.editoni.editor;

import lwjgui.LWJGUIApplication;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;

public class EditorApplication extends LWJGUIApplication
{
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final double PANEL_WIDTH = 250;

    private static long handleId;
    public static Context context;
    public static Runnable afterStart;

    public static int fps = 0;

    public static void main(String[] args, Runnable function)
    {
        ModernOpenGL = false;
        afterStart = function;
        launch(args);
    }

    @Override
    public void start(String[] args, Window window)
    {
        handleId = window.getContext().getWindowHandle();
        window.setTitle("Bumbo Editoni");
        window.setScene(new Scene(EditorGUI.INSTANCE.init(window), WIDTH, HEIGHT));
        context = window.getContext();
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSwapInterval(1);
        GLFW.glfwSetWindowPos(handleId, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);
        window.show();
        window.setRenderingCallback(new Renderer());

        afterStart.run();
    }

    public static long getWindowId()
    {
        return handleId;
    }

    static class Renderer implements lwjgui.gl.Renderer
    {
        private long frameStamp = System.currentTimeMillis();

        @Override
        public void render(Context context)
        {
            try
            {
                GL11.glClearColor(0.8f, 0.9f, 1.0f, 1.0f);
                GL11.glViewport((int) PANEL_WIDTH, 0, context.getWidth() - (int) PANEL_WIDTH * 2, context.getHeight());
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glCullFace(GL11.GL_NONE);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();

                double fovY = 90.0;
                double zNear = 0.01;
                double zFar = 1000.0;
                double aspect = (context.getWidth() - PANEL_WIDTH * 2) / (double) context.getHeight();
                double fH = Math.tan(fovY / 360 * Math.PI) * zNear;
                double fW = fH * aspect;
                GL11.glFrustum(-fW, fW, -fH, fH, zNear, zFar);

                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();

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
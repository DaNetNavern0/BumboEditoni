package me.danetnaverno.editoni.editor;

import lwjgui.LWJGUIApplication;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.layout.Pane;
import me.danetnaverno.editoni.Main;
import me.danetnaverno.editoni.Prototype;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;

public class EditorApplication extends LWJGUIApplication
{
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    private static long handleId;

    public static void main(String[] args)
    {
        ModernOpenGL = false;
        launch(args);
    }

    @Override
    public void start(String[] args, Window window)
    {
        handleId = window.getContext().getWindowHandle();

        Pane root = EditorGUI.init(window);

        window.setScene(new Scene(root, WIDTH, HEIGHT));
        window.show();

        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

        GLFW.glfwSetWindowPos(handleId, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);

        Prototype.init();

        window.setRenderingCallback(new Renderer());
    }

    public static long getWindowId()
    {
        return handleId;
    }

    static class Renderer implements lwjgui.gl.Renderer
    {
        @Override
        public void render(Context context)
        {
            GL11.glViewport(0, 0, context.getWidth(), context.getHeight());
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            //GL11.glFrontFace(GL11.GL_CCW);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();

            double fovY  = 90.0;
            double zNear = 0.01;
            double zFar  = 1000.0;
            double aspect = (double)context.getWidth() / (double)context.getHeight();
            double fH = Math.tan(fovY / 360 * Math.PI) * zNear;
            double fW = fH * aspect;
            GL11.glFrustum(-fW, fW, -fH, fH, zNear, zFar);

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();

            Main.displayLoop();
        }
    }
}
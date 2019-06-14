package me.danetnaverno.editoni.render;

import com.jogamp.opengl.glu.GLU;
import lwjgui.LWJGUIApplication;
import lwjgui.geometry.Pos;
import lwjgui.gl.Renderer;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.Menu;
import lwjgui.scene.control.MenuBar;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.VBox;
import me.danetnaverno.editoni.MainProcess;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class EditorApplication extends LWJGUIApplication
{
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    private static long handleId;
    public static Label label1;
    public static Label label2;
    public static Label label3;

    public static void main(String[] args)
    {
        ModernOpenGL = false;
        launch(args);
    }

    @Override
    public void start(String[] args, Window window)
    {
        handleId = window.getContext().getWindowHandle();

        // Create a simple pane
        BorderPane root = new BorderPane();
        root.setBackground(null);

        // Top part of borderpane
        {
            // Create Menu Bar
            MenuBar bar = new MenuBar();
            root.setTop(bar);

            // Create File Menu
            Menu file = new Menu("File");
            file.getItems().add(new MenuItem("New"));
            file.getItems().add(new MenuItem("Open"));
            file.getItems().add(new MenuItem("Save"));
            bar.getItems().add(file);

            // Create Edit Menu
            Menu edit = new Menu("Edit");
            edit.getItems().add(new MenuItem("Undo"));
            edit.getItems().add(new MenuItem("Redo"));
            bar.getItems().add(edit);
        }

        VBox vbox = new VBox();
        vbox.setSpacing(8);
        vbox.setAlignment(Pos.TOP_LEFT);
        // Add some text
        label1 = new Label("Hello World!");
        label1.setTextFill(Color.BLACK);
        vbox.getChildren().add(label1);
        label2 = new Label("Hello World!");
        label2.setTextFill(Color.BLACK);
        vbox.getChildren().add(label2);
        label3 = new Label("Hello World!");
        label3.setTextFill(Color.BLACK);
        vbox.getChildren().add(label3);
        root.setLeft(vbox);

        // Set the scene
        window.setScene(new Scene(root, WIDTH, HEIGHT));
        window.show();

        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

        GLFW.glfwSetWindowPos(handleId, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);

        MainProcess.initMainLoop();

        // Start the gears application
        window.setRenderingCallback(new GearsApplication());
    }

    public static long getWindowId()
    {
        return handleId;
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
        return new Vector3f(output.get(0), output.get(1), output.get(2));
    }

    static class GearsApplication implements Renderer
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

            MainProcess.displayLoop();
        }
    }
}
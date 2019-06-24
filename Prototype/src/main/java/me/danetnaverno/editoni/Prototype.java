package me.danetnaverno.editoni;

import me.danetnaverno.editoni.common.world.World;
import me.danetnaverno.editoni.editor.Camera;
import me.danetnaverno.editoni.editor.Editor;
import me.danetnaverno.editoni.editor.EditorApplication;
import me.danetnaverno.editoni.editor.InputHandler;
import me.danetnaverno.editoni.minecraft.MinecraftDictionaryFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.io.File;

public class Prototype
{
    public static Logger logger = LogManager.getLogger("Prototype");

    public static void init()
    {
        MinecraftDictionaryFiller.init();
        World world = Editor.INSTANCE.loadWorld(new File("data/1.14.2 world"));
        Editor.INSTANCE.setCurrentWorld(world);

        try
        {
            Camera.x = 0;
            Camera.y = 0;
            Camera.z = -20;
            Camera.pitch = 52;
            Camera.yaw = 140;

            InputHandler.init(EditorApplication.getWindowId());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void displayLoop()
    {
        GL11.glTranslated(Camera.x, Camera.y, Camera.z);
        GL11.glRotated(Camera.pitch, 1, 0, 0);
        GL11.glRotated(Camera.yaw, 0, 1, 0);

        Editor.INSTANCE.displayLoop();
        Editor.INSTANCE.controls();
    }
}
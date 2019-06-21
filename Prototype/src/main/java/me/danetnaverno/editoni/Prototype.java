package me.danetnaverno.editoni;

import me.danetnaverno.editoni.common.block.BlockType;
import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.editor.Camera;
import me.danetnaverno.editoni.editor.Editor;
import me.danetnaverno.editoni.editor.EditorApplication;
import me.danetnaverno.editoni.editor.InputHandler;
import me.danetnaverno.editoni.common.render.BlockRendererDictionary;
import me.danetnaverno.editoni.minecraft.MinecraftDictionaryFiller;
import me.danetnaverno.editoni.minecraft.world.MinecraftRegion;
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
        Editor.INSTANCE.loadWorld(new File("data/TestWorld"));

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

        for (MinecraftRegion region : Editor.INSTANCE.getWorld().regions.values())
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(region.x << 9, 0, region.z << 9);
            for (Chunk chunk : region.getChunks())
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(chunk.getRenderX() << 4, 0, chunk.getRenderZ() << 4);
                for (Block block : chunk.getBlocks())
                {
                    if (!block.getType().equals(BlockType.AIR))
                    {
                        GL11.glPushMatrix();
                        GL11.glTranslatef(block.getLocalPos().x, block.getLocalPos().y, block.getLocalPos().z);
                        block.getType().renderer.draw();
                        GL11.glPopMatrix();
                    }
                }
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();
        }

        Block block = Editor.INSTANCE.getSelectedBlock();
        if (block != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(block.getGlobalX(), block.getGlobalY(), block.getGlobalZ());
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1f, 1f, 0f, 0.7f);
            BlockRendererDictionary.ERROR.draw();
            GL11.glColor3f(1f, 1f, 1f);
            GL11.glPopMatrix();
        }
        Editor.INSTANCE.controls();
        Editor.INSTANCE.displayLoop();
    }
}
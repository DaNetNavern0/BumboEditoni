package me.danetnaverno.editoni;

import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.minecraft.world.MinecraftRegion;
import me.danetnaverno.editoni.minecraft.world.MinecraftWorld;
import me.danetnaverno.editoni.render.Camera;
import me.danetnaverno.editoni.render.LWJGLWindow;
import me.danetnaverno.editoni.render.Texture;
import net.querz.nbt.mca.MCAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Prototype
{
    public static Logger logger = LogManager.getLogger("Prototype");

    private static Texture mario;
    private static Texture brick;
    private static MinecraftWorld world;

    private static Pattern regex = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca");

    public static void init() throws IOException
    {
        //File file = new File("data/r.0.0.mca");
        //byte[] data = Files.readAllBytes(file.toPath());
        //MCAFile.readAll(file, new ByteArrayPointer(data));

        world = new MinecraftWorld();

        File worldFolder = new File("data/Test");
        File regionFolder = new File(worldFolder,"region");
        for (File file : regionFolder.listFiles())
        {
            Matcher matcher = regex.matcher(file.getName());
            if (matcher.matches())
            {
                int x = Integer.parseInt(matcher.group(1));
                int z = Integer.parseInt(matcher.group(2));
                world.addRegion(new MinecraftRegion(MCAUtil.readMCAFile(file), x, z));
            }
        }

        try
        {
            Camera.x = -4;
            Camera.y = 0;
            Camera.z = -36;
            Camera.pitch = 36;

            InputHandler.init(LWJGLWindow.window);

            mario = new Texture(Paths.get("data/sprites/actors/mario.png"));
            brick = new Texture(Paths.get("data/sprites/tiles/brick.png"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void displayLoop()
    {
        InputHandler.update();
        GL11.glTranslated(Camera.x, Camera.y, Camera.z);
        GL11.glRotated(Camera.pitch, 1, 0, 0);
        GL11.glRotated(Camera.yaw, 0, 1, 0);

        for (MinecraftRegion region : world.regions)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(region.x << 9, 0, region.z << 9);
            for (Chunk chunk : region.chunks)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(chunk.x << 4, 0, chunk.z << 4);
                for (Block block : chunk.getBlocks())
                {
                    if (!block.id.equalsIgnoreCase("minecraft:air"))
                    {
                        GL11.glPushMatrix();
                        GL11.glTranslatef(block.x, block.y, block.z);
                        if (block.id.equalsIgnoreCase("minecraft:stone"))
                            brick.bind();
                        else
                            mario.bind();
                        drawCube();
                        GL11.glPopMatrix();
                    }
                }
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();
        }
    }

    private static void drawCube()
    {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(0.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(0.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(0.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(1.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(1.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(1.0f, 0.0f, 0.0f);
        GL11.glEnd();
    }

    public static void mainLoop()
    {
        if (InputHandler.keyDown(GLFW.GLFW_KEY_A))
            Camera.x+=4;
        if (InputHandler.keyDown(GLFW.GLFW_KEY_D))
            Camera.x-=4;
        else if (InputHandler.keyDown(GLFW.GLFW_KEY_W))
            Camera.z+=4;
        else if (InputHandler.keyDown(GLFW.GLFW_KEY_S))
            Camera.z-=4;
        else if (InputHandler.keyDown(GLFW.GLFW_KEY_E))
            Camera.y-=4;
        else if (InputHandler.keyDown(GLFW.GLFW_KEY_Q))
            Camera.y+=4;
        else if (InputHandler.keyDown(GLFW.GLFW_KEY_1))
            Camera.yaw-=4;
        else if (InputHandler.keyDown(GLFW.GLFW_KEY_2))
            Camera.yaw+=4;
        else if (InputHandler.keyDown(GLFW.GLFW_KEY_3))
            Camera.pitch-=4;
        else if (InputHandler.keyDown(GLFW.GLFW_KEY_4))
            Camera.pitch+=4;
    }
}
package me.danetnaverno.editoni;

import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.minecraft.world.MinecraftRegion;
import me.danetnaverno.editoni.minecraft.world.MinecraftWorld;
import me.danetnaverno.editoni.common.render.BlockRendererCube;
import me.danetnaverno.editoni.render.Camera;
import me.danetnaverno.editoni.render.LWJGLWindow;
import me.danetnaverno.editoni.common.texture.Texture;
import net.querz.nbt.mca.MCAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.danetnaverno.editoni.render.LWJGLWindow.GetOGLPos;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

public class Prototype
{
    public static Logger logger = LogManager.getLogger("Prototype");

    private static MinecraftWorld world;

    private static Pattern regex = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca");

    public static void init() throws IOException
    {
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void displayLoop()
    {
        try
        {
            InputHandler.update();
            GL11.glTranslated(Camera.x, Camera.y, Camera.z);
            GL11.glRotated(Camera.pitch, 1, 0, 0);
            GL11.glRotated(Camera.yaw, 0, 1, 0);

            for (MinecraftRegion region : world.regions.values())
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(region.x << 9, 0, region.z << 9);
                for (Chunk chunk : region.chunks.values())
                {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(chunk.xRender << 4, 0, chunk.zRender << 4);
                    for (Block block : chunk.getBlocks())
                    {
                        if (!block.type.getId().equalsIgnoreCase("minecraft:air"))
                        {
                            GL11.glPushMatrix();
                            GL11.glTranslatef(block.getLocalX(), block.getLocalY(), block.getLocalZ());
                            block.type.getRenderer().draw();
                            GL11.glPopMatrix();
                        }
                    }
                    GL11.glPopMatrix();
                }
                GL11.glPopMatrix();
            }

            if (InputHandler.keyDown(org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE))
            {
                Block block1 = world.getBlockAt(-1, 6, 0);
                DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(LWJGLWindow.window, x, y);
                Vector3f ass = GetOGLPos((int) x.get(0), (int) y.get(0));
                Block block = findBlock(ass);
                if (block != null)
                {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(block.getGlobalX(), block.getGlobalY(), block.getGlobalZ());
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glColor3f(1f, 1f, 0f);
                    new BlockRendererCube(new Texture(Paths.get("data/textures/blocks/error.png"))).draw();
                    GL11.glColor3f(1f, 1f, 1f);
                    GL11.glPopMatrix();
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    private static Block findBlock(Vector3f point)
    {
        Vector3i floor = new Vector3i((int) Math.floor(point.x) - 1, (int) Math.floor(point.y) - 1, (int) Math.floor(point.z) - 1);
        Vector3i ceiling = new Vector3i((int) Math.ceil(point.x) + 1, (int) Math.ceil(point.y) + 1, (int) Math.ceil(point.z) + 1);

        Block closest = null;
        float min = Float.MAX_VALUE;

        for (int x = floor.x; x <= ceiling.x; x++)
            for (int y = floor.y; y <= ceiling.y; y++)
                for (int z = floor.z; z <= ceiling.z; z++)
                {
                    float distance = point.distanceSquared(x + 0.5f, y + 0.5f, z + 0.5f);
                    if (distance < min)
                    {
                        Block block = world.getBlockAt(x, y, z);
                        if (block != null && !block.type.getId().equalsIgnoreCase("minecraft:air"))
                        {
                            closest = block;
                            min = distance;
                        }
                    }
                }
        return closest;
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
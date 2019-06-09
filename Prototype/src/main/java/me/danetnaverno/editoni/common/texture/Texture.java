package me.danetnaverno.editoni.common.texture;

import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class Texture
{
    public final int target = GL_TEXTURE_2D;
    public final int id;
    public final int width;
    public final int height;

    public static final int LINEAR = GL_LINEAR;
    public static final int NEAREST = GL_NEAREST;

    public static final int CLAMP = GL_CLAMP;
    public static final int CLAMP_TO_EDGE = GL_CLAMP_TO_EDGE;
    public static final int REPEAT = GL_REPEAT;

    public Texture(Path path) throws IOException
    {
        ByteBuffer imageData = ioResourceToByteBuffer(path);

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer components = stack.mallocInt(1);

            ByteBuffer decodedImage = STBImage.stbi_load_from_memory(imageData, w, h, components, 4);

            this.width = w.get();
            this.height = h.get();

            this.id = glGenTextures();

            glBindTexture(GL_TEXTURE_2D, this.id);

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, decodedImage);

            GL30.glGenerateMipmap(GL_TEXTURE_2D);
        }
    }

    public ByteBuffer ioResourceToByteBuffer(Path path) throws IOException
    {
        ByteBuffer buffer;
        File file = path.toFile();
        try (FileChannel fc = new FileInputStream(file).getChannel())
        {
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        }
        return buffer;
    }


    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void draw(float x, float y, float width, float height)
    {
        draw(x, y, width, height, 0, 0, 1, 1);
    }

    public void draw(float x, float y, float width, float height, float u, float v, float u2, float v2)
    {
        glBindTexture(GL_TEXTURE_2D, id);

        glColor4f(1f, 1f, 1f, 1f);
        glBegin(GL_QUADS);
        glTexCoord2f(u, v2);
        glVertex3f(x, y, 0);
        glTexCoord2f(u, v);
        glVertex3f(x, y + height, 0);
        glTexCoord2f(u2, v);
        glVertex3f(x + width, y + height, 0);
        glTexCoord2f(u2, v2);
        glVertex3f(x + width, y, 10);
        glEnd();
    }
}
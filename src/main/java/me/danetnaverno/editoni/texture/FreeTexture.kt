package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.util.ResourceLocation
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path

class FreeTexture(path: Path)
{
    var id: Int = 0
        private set
    var width: Int = 0
        private set
    var height: Int = 0
        private set

    init
    {
        val imageData = ioResourceToByteBuffer(path)

        val stack = MemoryStack.stackPush()
        try
        {
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val components = stack.mallocInt(1)

            val decodedImage = STBImage.stbi_load_from_memory(imageData, w, h, components, 4)

            this.width = w.get()
            this.height = h.get()

            id = glGenTextures()

            glBindTexture(GL_TEXTURE_3D, id)

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_REPEAT)

            glTexImage3D(GL_TEXTURE_3D, 0, GL_RGBA, 16, 16, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, decodedImage)
            glGenerateMipmap(GL_TEXTURE_3D)
        }
        finally
        {
            stack.close()
        }
    }

    fun bind()
    {
        glBindTexture(GL_TEXTURE_3D, id)
    }

    fun ioResourceToByteBuffer(path: Path): ByteBuffer
    {
        val bytes = Files.readAllBytes(path)
        val buffer = BufferUtils.createByteBuffer(bytes.size)
        buffer.put(bytes, 0, bytes.size)
        buffer.flip()
        return buffer
    }

    companion object
    {
        operator fun get(name: ResourceLocation): FreeTexture
        {
            return FreeTextureDictionary[name]
        }
    }
}

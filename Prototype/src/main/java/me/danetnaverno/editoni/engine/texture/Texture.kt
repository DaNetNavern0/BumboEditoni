package me.danetnaverno.editoni.engine.texture

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path

class Texture @Throws(IOException::class) internal constructor(path: Path)
{
    private var id: Int = 0
    var width: Int = 0
        private set
    var height: Int = 0
        private set

    init
    {
        val imageData = ioResourceToByteBuffer(path)

        MemoryStack.stackPush().use { stack ->
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val components = stack.mallocInt(1)

            val decodedImage = STBImage.stbi_load_from_memory(imageData, w, h, components, 4)

            this.width = w.get()
            this.height = h.get()

            this.id = glGenTextures()

            glBindTexture(GL_TEXTURE_2D, this.id)

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, decodedImage)

            GL30.glGenerateMipmap(GL_TEXTURE_2D)
        }
    }

    fun bind()
    {
        glBindTexture(GL_TEXTURE_2D, id)
    }

    @Throws(IOException::class)
    fun ioResourceToByteBuffer(path: Path): ByteBuffer
    {
        FileInputStream(path.toFile()).channel.use { fc ->
            return fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
        }
    }

    companion object
    {
        @JvmStatic
        operator fun get(name: String): Texture
        {
            return TextureDictionary[name]
        }
    }
}
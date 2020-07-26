package me.danetnaverno.editoni.texture

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path


class TextureImpl(path: Path) : Texture()
{
    private var _id: Int = 0
    override val id: Int
        get() = _id

    var width: Int = 0
        private set
    var height: Int = 0
        private set

    init
    {
        val imageData = ioResourceToByteBuffer(path)

        val stack = MemoryStack.stackPush() //use
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val components = stack.mallocInt(1)

            val decodedImage = STBImage.stbi_load_from_memory(imageData, w, h, components, 4)

            this.width = w.get()
            this.height = h.get()

            this._id = glGenTextures()

            glBindTexture(GL_TEXTURE_2D, this.id)

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, decodedImage)
            GL30.glGenerateMipmap(GL_TEXTURE_2D)
        stack.close() //todo use
    }

    override fun bind()
    {
        glBindTexture(GL_TEXTURE_2D, id)
    }

    fun ioResourceToByteBuffer(path: Path): ByteBuffer
    {
        val bytes = Files.readAllBytes(path)
        val buffer = BufferUtils.createByteBuffer(bytes.size)
        buffer.put(bytes, 0, bytes.size)
        buffer.flip()
        return buffer
    }
}

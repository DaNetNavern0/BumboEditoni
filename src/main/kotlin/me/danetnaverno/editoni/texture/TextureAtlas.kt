package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.editor.Editor
import org.lwjgl.opengl.ARBTextureStorage.glTexStorage3D
import org.lwjgl.opengl.GL33.*
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.file.Files

typealias TextureId = Int

class TextureAtlas constructor(textures: Collection<Texture>)
{
    var atlasTexture : TextureId = 0
        private set

    init
    {
        val texId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D_ARRAY, texId)

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT)

        glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, 16, 16, textures.size)

        var buffer = MemoryUtil.memAlloc(4096)
        try
        {
            MemoryStack.stackPush().use { memoryStack ->
                val trash = memoryStack.mallocInt(1)
                for ((i, texture) in textures.withIndex())
                {
                    try
                    {
                        val bytes = Files.readAllBytes(texture.path)

                        if (bytes.size > buffer.capacity())
                        {
                            MemoryUtil.memFree(buffer)
                            buffer = MemoryUtil.memAlloc(bytes.size)
                        }
                        else if (bytes.size > buffer.limit())
                            buffer.limit(bytes.size)

                        buffer.put(bytes, 0, bytes.size)
                        buffer.flip()

                        val decodedImage = STBImage.stbi_load_from_memory(buffer, trash, trash, trash, 4)!!
                        try { glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, 16, 16, 1, GL_RGBA, GL_UNSIGNED_BYTE, decodedImage) }
                        finally { STBImage.stbi_image_free(decodedImage) }
                        texture.atlasZLayer = i.toFloat()
                    }
                    catch (e: Exception)
                    {
                        Editor.logger.error("Failed to load a texture: $texture", e)
                    }
                }
            }
        }
        finally
        {
            MemoryUtil.memFree(buffer)
        }

        atlasTexture = texId
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0)
    }

    fun bind()
    {
        glBindTexture(GL_TEXTURE_2D_ARRAY, atlasTexture)
    }

    companion object
    {
        lateinit var mainAtlas: TextureAtlas
    }
}
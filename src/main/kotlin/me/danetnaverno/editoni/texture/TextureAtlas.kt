package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.util.ResourceUtil
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.ARBTextureStorage.glTexStorage3D
import org.lwjgl.opengl.GL33.*
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

class TextureAtlas constructor(private val textures: Map<String, Texture>)
{
    private var atlasTexture = 0

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
                for ((i, texture) in textures.values.withIndex())
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
                        logger.error("Failed to load a texture: $texture", e)
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

    /**
     * Because we don't call this method within the rendering loop,
     * we can get away with using [String] as an argument and as a Map key
     */
    operator fun get(name: String): Texture
    {
        val texture = textures[name]
        if (texture != null)
            return texture

        logger.error("Texture has never been loaded: $name")
        return get("common:error")
    }

    companion object
    {
        val logger = LogManager.getLogger("TextureAtlas")!!

        var mainAtlas: TextureAtlas

        init
        {
            val root = ResourceUtil.getBuiltInResourcePath("/assets/textures")
            val textures = Files.walk(Paths.get(root.toUri()))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toMap(
                            { ResourceUtil.toResourceLocation(it, root) },
                            { Texture(it) }))
            mainAtlas = TextureAtlas(textures)
        }
    }
}
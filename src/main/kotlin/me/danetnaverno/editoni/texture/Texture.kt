package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.util.ResourceLocation
import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path

class Texture(val location: ResourceLocation, path: Path)
{
    var width: Int = 0
        private set
    var height: Int = 0
        private set
    var decodedImage: ByteBuffer?
        private set

    init
    {
        val imageData = ioResourceToByteBuffer(path)

        MemoryStack.stackPush().use { memoryStack ->
            val w = memoryStack.mallocInt(1)
            val h = memoryStack.mallocInt(1)
            val components = memoryStack.mallocInt(1)

            decodedImage = STBImage.stbi_load_from_memory(imageData, w, h, components, 4)

            this.width = w.get()
            this.height = h.get()
        }
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
        operator fun get(name: ResourceLocation): Texture
        {
            return TextureDictionary[name]
        }
    }
}
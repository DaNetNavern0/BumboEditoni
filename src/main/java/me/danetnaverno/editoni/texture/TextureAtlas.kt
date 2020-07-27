package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL15
import java.io.IOException

typealias TextureId = Int

/**
 * Yes, we use a GL_TEXTURE_3D to make an easy to use atlas.
 * Not something a professional game engine would use, especially giving that we're dodging the need to use shaders,
 *  but it will work just fine for this application... At least for now
 */
class TextureAtlas constructor(textures: Collection<Texture>)
{
    var atlasTexture : TextureId = 0
        private set
    private var zLayerMap = mutableMapOf<ResourceLocation, Int>()

    init
    {
        val texId = GL15.glGenTextures()
        GL15.glBindTexture(GL15.GL_TEXTURE_3D, texId)

        GL15.glPixelStorei(GL15.GL_UNPACK_ALIGNMENT, 1)
        GL15.glTexParameteri(GL15.GL_TEXTURE_3D, GL15.GL_TEXTURE_MIN_FILTER, GL15.GL_NEAREST)
        GL15.glTexParameteri(GL15.GL_TEXTURE_3D, GL15.GL_TEXTURE_MAG_FILTER, GL15.GL_NEAREST)

        GL12.glTexImage3D(GL15.GL_TEXTURE_3D, 0, GL15.GL_RGBA, 16, 16, textures.size, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0)
        for ((i, texture) in textures.withIndex())
        {
            zLayerMap[texture.location] = i
            try
            {
                if (texture.decodedImage != null)
                    GL12.glTexSubImage3D(GL15.GL_TEXTURE_3D, 0, 0, 0, i, 16, 16, 1, GL15.GL_RGBA, GL15.GL_UNSIGNED_BYTE, texture.decodedImage)
            }
            catch (e: IOException)
            {
                e.printStackTrace()
            }
        }

        GL15.glTexParameteri(GL15.GL_TEXTURE_3D, GL15.GL_TEXTURE_WRAP_S, GL15.GL_CLAMP_TO_EDGE)
        GL15.glTexParameteri(GL15.GL_TEXTURE_3D, GL15.GL_TEXTURE_WRAP_T, GL15.GL_CLAMP_TO_EDGE)
        GL15.glTexParameteri(GL15.GL_TEXTURE_3D, GL15.GL_TEXTURE_WRAP_R, GL15.GL_CLAMP_TO_EDGE)

        atlasTexture = texId
    }

    fun getZLayer(texture: Texture) : Float
    {
        return zLayerMap.getOrDefault(texture.location, 0).toFloat() / zLayerMap.size.toFloat() + 0.001f
    }

    companion object
    {
        lateinit var todoInstance: TextureAtlas
    }
}
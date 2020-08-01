package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.util.ResourceLocation
import org.lwjgl.opengl.ARBTextureStorage.glTexStorage3D
import org.lwjgl.opengl.GL30.*
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
    var zLayerMap = mutableMapOf<ResourceLocation, Int>()

    init
    {
        glActiveTexture(GL_TEXTURE0)
        val texId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D_ARRAY, texId)

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT)

        glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, 16, 16, textures.size)
        for ((i, texture) in textures.withIndex())
        {
            zLayerMap[texture.location] = i
            try
            {
                if (texture.decodedImage != null)
                    glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, 16, 16, 1, GL_RGBA, GL_UNSIGNED_BYTE, texture.decodedImage)
            }
            catch (e: IOException)
            {
                e.printStackTrace()
            }
        }

        atlasTexture = texId
    }

    fun getZLayer(texture: Texture) : Float
    {
        return zLayerMap.getOrDefault(texture.location, 0).toFloat()
    }

    companion object
    {
        lateinit var mainAtlas: TextureAtlas
    }
}
package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.util.ResourceLocation
import me.danetnaverno.editoni.util.ResourceUtil
import org.apache.logging.log4j.LogManager

object FreeTextureDictionary
{
    private val logger = LogManager.getLogger("TextureDictionary")
    private val textures = mutableMapOf<ResourceLocation, FreeTexture>()

    operator fun get(name: ResourceLocation): FreeTexture
    {
        var texture = textures[name]
        if (texture != null)
            return texture

        texture = try
        {
            FreeTexture(ResourceUtil.getBuiltInResourcePath("/assets/textures/${name.domain}/${name.path}.png"))
        }
        catch (ex: Exception)
        {
            logger.error("Failed to load a texture: $name", ex)
            get(ResourceLocation("common:error"))
        }
        textures[name] = texture

        return texture
    }

    fun getTextures(): MutableCollection<FreeTexture>
    {
        return textures.values
    }
}
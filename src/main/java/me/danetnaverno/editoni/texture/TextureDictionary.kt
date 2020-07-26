package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.util.ResourceUtil
import org.apache.logging.log4j.LogManager

object TextureDictionary
{
    private val logger = LogManager.getLogger("TextureDictionary")
    private val textures = mutableMapOf<ResourceLocation, Texture>()

    @JvmStatic
    operator fun get(name: ResourceLocation): Texture
    {
        var texture = textures[name]
        if (texture != null)
            return texture

        texture = try
        {
            TextureImpl(ResourceUtil.getBuiltInResourcePath("/assets/textures/${name.domain}/${name.path}.png"))
        }
        catch (ex: Exception)
        {
            logger.error("Failed to load a texture: $name", ex)
            get(ResourceLocation("common:error"))
        }
        textures[name] = texture
        return texture
    }

    interface ITextureProvider
    {
        fun provide(name: ResourceLocation): Texture
    }
}
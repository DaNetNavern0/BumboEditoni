package me.danetnaverno.editoni.engine.texture

import org.apache.logging.log4j.LogManager
import java.nio.file.Paths

object TextureDictionary
{
    private val logger = LogManager.getLogger("TextureDictionary")
    private val textures = mutableMapOf<String, Texture>()

    @JvmStatic
    operator fun get(name: String): Texture
    {
        var texture = textures[name]
        if (texture == null)
        {
            try
            {
                texture = Texture(Paths.get("data/textures/$name.png"))
            }
            catch (ex: Exception)
            {
                logger.error("Failed to load a texture: $name", ex)
                texture = get("error")
            }
            textures[name] = texture!!
        }
        return texture
    }
}
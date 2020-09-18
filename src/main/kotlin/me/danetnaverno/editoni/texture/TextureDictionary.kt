package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.util.ResourceLocation
import me.danetnaverno.editoni.util.ResourceUtil
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.util.stream.Collectors

object TextureDictionary
{
    private val logger = LogManager.getLogger("TextureDictionary")
    private val textures = mutableMapOf<ResourceLocation, Texture>()

    init
    {
        //Yes, this isn't a good code, but I want to move on to having some functionality already
        //todo make this part properly
        val root = ResourceUtil.getBuiltInResourcePath("/assets/textures/")
        val textures = Files.find(root, 99, { path, basicFileAttributes ->
            path.getName(path.nameCount - 1).toString().endsWith(".png")
        }).map { path ->
            val ass = path.getName(path.nameCount - 1).toString()
            val resourceLocation = ResourceLocation.fromTwo(path.getName(path.nameCount - 2).toString(), ass.substring(0, ass.length - 4))
            Texture(resourceLocation, path)
        }.collect(Collectors.toList())
        TextureAtlas.mainAtlas = TextureAtlas(textures)
    }

    operator fun get(name: ResourceLocation): Texture
    {
        var texture = textures[name]
        if (texture != null)
            return texture

        texture = try
        {
            Texture(name, ResourceUtil.getBuiltInResourcePath("/assets/textures/${name.name.replace(":","/")}.png"))
        }
        catch (ex: Exception)
        {
            logger.error("Failed to load a texture: $name", ex)
            get(ResourceLocation("common:error"))
        }
        textures[name] = texture!!

        return texture
    }

    fun getTextures(): MutableCollection<Texture>
    {
        return textures.values
    }

    interface ITextureProvider
    {
        fun provide(name: ResourceLocation): Texture
    }
}
package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.util.ResourceUtil
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.asSequence

object TextureDictionary
{
    private val textures: Map<String, Texture>

    init
    {
        val root = ResourceUtil.getBuiltInResourcePath("/assets/textures")
        textures = Files.walk(Paths.get(root.toUri()))
                .filter(Files::isRegularFile)
                .asSequence().associateBy(
                        {
                            val nameStr = root.relativize(it).toString().replace('\\', ':', false).replace('/', ':', false)
                            nameStr.substring(0, nameStr.length - 4)
                        },
                        { Texture(it) })
        TextureAtlas.mainAtlas = TextureAtlas(textures.values)
    }

    /**
     * Because we don't call this method within the rendering loop,
     * we can get away with using [String] as an argument and a Map key
     */
    operator fun get(name: String): Texture
    {
        val texture = textures[name]
        if (texture != null)
            return texture

        Editor.logger.error("Texture has never been loaded: $name")
        return get("common:error")
    }
}
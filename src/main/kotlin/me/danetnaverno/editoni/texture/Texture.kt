package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.util.ResourceLocation
import java.nio.file.Path

class Texture(val path: Path)
{
    var atlasZLayer: Float = 0f
        internal set

    override fun toString(): String
    {
        return "Texture($path)"
    }

    companion object
    {
        inline operator fun get(name: ResourceLocation): Texture
        {
            return TextureDictionary[name]
        }
    }
}
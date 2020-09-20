package me.danetnaverno.editoni.texture

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
        inline operator fun get(name: String): Texture
        {
            return TextureDictionary[name]
        }
    }
}
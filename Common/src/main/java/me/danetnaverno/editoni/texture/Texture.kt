package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.common.ResourceLocation
import java.nio.file.Path

abstract class Texture protected constructor(path: Path)
{
    abstract val id : Int
    abstract fun bind()

    companion object
    {
        @JvmStatic
        operator fun get(name: ResourceLocation): Texture
        {
            return TextureDictionary[name]
        }
    }
}

package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.common.ResourceLocation

abstract class Texture
{
    abstract val id: Int
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

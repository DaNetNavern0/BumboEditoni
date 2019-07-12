package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.common.ResourceLocation
import java.nio.file.Paths

object TextureProvider : TextureDictionary.ITextureProvider
{
    override fun provide(name: ResourceLocation): Texture
    {
        return TextureImpl(Paths.get("data/textures/${name.domain}/${name.path}.png"))
    }
}
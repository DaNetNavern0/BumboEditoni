package me.danetnaverno.editoni.texture

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.util.ResourceUtil

object TextureProvider : TextureDictionary.ITextureProvider
{
    override fun provide(name: ResourceLocation): Texture
    {
        return TextureImpl(ResourceUtil.getBuiltInResourcePath("/assets/textures/${name.domain}/${name.path}.png"))
    }
}
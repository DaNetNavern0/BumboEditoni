package me.danetnaverno.editoni.minecraft.blockrender

import me.danetnaverno.editoni.common.blockrender.BlockRendererCube
import me.danetnaverno.editoni.texture.Texture

class BlockRendererMinecraftChest : BlockRendererCube
{
    @Suppress("unused")
    constructor()

    @Suppress("unused")
    constructor(texture: Texture)
    {
        plainTexture(texture)
    }

    @Suppress("unused")
    constructor(top: Texture, bottom: Texture, side: Texture)
    {
        withSideTexture(top, bottom, side)
    }

    @Suppress("unused")
    constructor(top: Texture, bottom: Texture, north: Texture, west: Texture, south: Texture, east: Texture)
    {
        sixTextures(top, bottom, north, west, south, east)
    }

    override fun getSize(): Float
    {
        return 0.8f
    }
}

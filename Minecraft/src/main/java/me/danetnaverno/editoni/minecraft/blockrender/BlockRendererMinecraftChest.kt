package me.danetnaverno.editoni.minecraft.blockrender

import me.danetnaverno.editoni.common.blockrender.BlockRendererCube
import me.danetnaverno.editoni.texture.Texture

class BlockRendererMinecraftChest : BlockRendererCube
{
    constructor()

    constructor(texture: Texture)
    {
        plainTexture(texture)
    }

    constructor(top: Texture, bottom: Texture, side: Texture)
    {
        withSideTexture(top, bottom, side)
    }

    constructor(top: Texture, bottom: Texture, north: Texture, west: Texture, south: Texture, east: Texture)
    {
        sixTextures(top, bottom, north, west, south, east)
    }

    override fun getSize(): Float
    {
        return 0.8f
    }
}

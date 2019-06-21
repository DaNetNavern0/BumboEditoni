package me.danetnaverno.editoni.minecraft.render

import me.danetnaverno.editoni.common.render.BlockRendererCube
import me.danetnaverno.editoni.engine.texture.Texture

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

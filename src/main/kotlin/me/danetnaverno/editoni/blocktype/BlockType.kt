package me.danetnaverno.editoni.blocktype

import me.danetnaverno.editoni.util.ResourceLocation
import me.danetnaverno.editoni.render.blockrender.BlockRenderer

open class BlockType(val id: ResourceLocation, val renderer: BlockRenderer, val isOpaque: Boolean, val isHidden: Boolean)
{
    override fun toString(): String
    {
        return id.toString()
    }
}

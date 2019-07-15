package me.danetnaverno.editoni.common.blocktype

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.BlockRenderer

open class BlockType(val id: ResourceLocation, val renderer: BlockRenderer, val isOpaque: Boolean)
{
    override fun toString(): String
    {
        return id.toString()
    }
}

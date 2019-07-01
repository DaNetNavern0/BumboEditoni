package me.danetnaverno.editoni.common.blocktype

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.BlockRenderer
import me.danetnaverno.editoni.editor.Editor

open class BlockType(val id: ResourceLocation, val renderer: BlockRenderer)
{
    override fun toString(): String
    {
        return id.toString()
    }

    companion object
    {
        @JvmStatic
        val airType : BlockType
            get() = Editor.currentWorld.airType
    }
}

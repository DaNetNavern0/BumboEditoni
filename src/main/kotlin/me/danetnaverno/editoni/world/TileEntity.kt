package me.danetnaverno.editoni.world

import net.querz.nbt.tag.CompoundTag

class TileEntity(val tag: CompoundTag)
{
    override fun toString(): String
    {
        return tag.toString()
    }
}

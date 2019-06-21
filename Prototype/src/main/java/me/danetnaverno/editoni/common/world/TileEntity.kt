package me.danetnaverno.editoni.common.world

import net.querz.nbt.CompoundTag

abstract class TileEntity(val tag: CompoundTag)
{
    override fun toString(): String
    {
        return tag.toTagString()
    }
}

package me.danetnaverno.editoni.common.world

import net.querz.nbt.CompoundTag

abstract class BlockState(val properties: CompoundTag)
{
    override fun toString(): String
    {
        return properties.toTagString()
    }
}
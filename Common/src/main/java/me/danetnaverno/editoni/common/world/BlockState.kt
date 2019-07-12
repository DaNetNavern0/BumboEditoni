package me.danetnaverno.editoni.common.world

import net.querz.nbt.CompoundTag

abstract class BlockState(val tag: CompoundTag)
{
    override fun toString(): String
    {
        return tag.toTagString()
    }
}

class UnknownBlockState(properties: CompoundTag) : BlockState(properties)
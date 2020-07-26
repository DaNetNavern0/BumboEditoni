package me.danetnaverno.editoni.world

import net.querz.nbt.tag.CompoundTag


abstract class BlockState(val tag: CompoundTag)
{
    override fun toString(): String
    {
        return tag.toString()
    }
}

class UnknownBlockState(properties: CompoundTag) : BlockState(properties)
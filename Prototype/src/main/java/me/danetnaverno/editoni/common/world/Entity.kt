package me.danetnaverno.editoni.common.world

import net.querz.nbt.CompoundTag

open class Entity(val tag: CompoundTag)
{
    override fun toString(): String
    {
        return tag.toTagString()
    }
}

package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.location.EntityLocation
import net.querz.nbt.tag.CompoundTag

abstract class Entity(val chunk: Chunk, val entityLocation: EntityLocation, val type: Any, val tag: CompoundTag)
{
    override fun toString(): String
    {
        return tag.toString()
    }
}

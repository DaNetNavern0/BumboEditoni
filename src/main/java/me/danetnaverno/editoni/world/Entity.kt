package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.util.location.EntityLocation
import net.querz.nbt.tag.CompoundTag


abstract class Entity(val chunk: Chunk, val location: EntityLocation, val type: Object, val tag: CompoundTag)
{
    override fun toString(): String
    {
        return tag.toString()
    }
}

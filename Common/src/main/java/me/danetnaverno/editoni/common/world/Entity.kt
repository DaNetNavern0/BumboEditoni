package me.danetnaverno.editoni.common.world

import me.danetnaverno.editoni.common.entitytype.EntityType
import me.danetnaverno.editoni.util.location.EntityLocation
import net.querz.nbt.CompoundTag

abstract class Entity(val chunk: Chunk, val location: EntityLocation, val type: EntityType, val tag: CompoundTag)
{
    override fun toString(): String
    {
        return tag.toTagString()
    }
}

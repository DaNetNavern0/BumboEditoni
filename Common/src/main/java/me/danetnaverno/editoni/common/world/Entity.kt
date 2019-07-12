package me.danetnaverno.editoni.common.world

import me.danetnaverno.editoni.common.entitytype.EntityType
import net.querz.nbt.CompoundTag
import org.joml.Vector3d

abstract class Entity(val chunk: Chunk, val globalPos: Vector3d, val type: EntityType, val tag: CompoundTag)
{
    override fun toString(): String
    {
        return tag.toTagString()
    }
}

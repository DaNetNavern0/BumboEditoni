package me.danetnaverno.editoni.common.entitytype

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.EntityRenderer

open class EntityType(val id: ResourceLocation, val renderer: EntityRenderer)
{
    override fun toString(): String
    {
        return id.toString()
    }
}

package me.danetnaverno.editoni.blockstate

import net.querz.nbt.tag.CompoundTag

class MinecraftSignBlockState(properties: CompoundTag) : BlockState(properties)
{
    val facing: String
    val waterlogged: Boolean

    init
    {
        facing = properties.getString("facing")
        waterlogged = properties.getBoolean("waterlogged")
    }
}
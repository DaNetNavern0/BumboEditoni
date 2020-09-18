package me.danetnaverno.editoni.blockstate

import net.querz.nbt.tag.CompoundTag

class MinecraftChestBlockState(properties: CompoundTag) : BlockState(properties)
{
    val facing: String
    val type: String
    val waterlogged: Boolean

    init
    {
        facing = properties.getString("facing")
        waterlogged = properties.getBoolean("waterlogged")
        type = properties.getString("type")
    }
}
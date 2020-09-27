package me.danetnaverno.editoni.blockstate

import net.querz.nbt.tag.CompoundTag

class MinecraftChestBlockState(properties: CompoundTag) : BlockState(properties)
{
    val facing = properties.getString("facing")
    val type = properties.getString("type")
    val waterlogged = properties.getBoolean("waterlogged")
}
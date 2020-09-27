package me.danetnaverno.editoni.blockstate

import net.querz.nbt.tag.CompoundTag

class MinecraftSignBlockState(properties: CompoundTag) : BlockState(properties)
{
    val facing = properties.getString("facing")
    val waterlogged = properties.getBoolean("waterlogged")

}
package me.danetnaverno.editoni.blockstate

import net.querz.nbt.tag.CompoundTag

class MinecraftLiquidState(properties: CompoundTag) : BlockState(properties)
{
    val level = properties.getString("level").toInt() //properties.getInt("level") loves to throw an Exception for some reason
}
package me.danetnaverno.editoni.blockstate

import net.querz.nbt.tag.CompoundTag

class MinecraftLiquidState(properties: CompoundTag) : BlockState(properties)
{
    val level: Int

    init
    {
        level = properties.getString("level").toInt()
    }
}
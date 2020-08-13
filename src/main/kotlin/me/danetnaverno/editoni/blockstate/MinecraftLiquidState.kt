package me.danetnaverno.editoni.blockstate

import me.danetnaverno.editoni.world.BlockState
import net.querz.nbt.tag.CompoundTag

class MinecraftLiquidState(properties: CompoundTag) : BlockState(properties)
{
    val level: Int

    init
    {
        level = properties.getString("level").toInt()
    }
}
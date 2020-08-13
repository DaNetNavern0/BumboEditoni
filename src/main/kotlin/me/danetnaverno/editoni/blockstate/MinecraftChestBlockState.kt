package me.danetnaverno.editoni.blockstate

import me.danetnaverno.editoni.world.BlockState
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
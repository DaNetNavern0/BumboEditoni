package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.world.TileEntity
import net.querz.nbt.CompoundTag

class MinecraftTileEntity(chunk: MinecraftChunk, x: Int, y: Int, z: Int, tag: CompoundTag) : TileEntity(chunk, x, y, z, tag)
{
    init
    {
    }
}
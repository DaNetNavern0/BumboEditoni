package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.world.TileEntity
import net.querz.nbt.CompoundTag

open class MinecraftTileEntity(chunk: MinecraftChunk, chunkX: Int, chunkY: Int, chunkZ: Int, tag: CompoundTag)
    : TileEntity(chunk, chunkX, chunkY, chunkZ, tag)

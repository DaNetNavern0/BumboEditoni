package me.danetnaverno.editoni.common.world

import net.querz.nbt.CompoundTag

abstract class TileEntity(val chunk: Chunk, val localX: Int, val localY: Int, val localZ: Int, val tag: CompoundTag)
{
    val globalX: Int
        get() = chunk.xPos shl 4 or (localX and 15)

    val globalY: Int
        get() = localY

    val globalZ: Int
        get() = chunk.zPos shl 4 or (localZ and 15)
}

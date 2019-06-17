package me.danetnaverno.editoni.common.world

import net.querz.nbt.CompoundTag

abstract class TileEntity(val chunk: Chunk, val chunkX: Int, val chunkY: Int, val chunkZ: Int, val tag: CompoundTag)
{
    val globalX: Int
        get() = chunk.xPos shl 4 or (chunkX and 15)

    val globalY: Int
        get() = chunkY

    val globalZ: Int
        get() = chunk.zPos shl 4 or (chunkZ and 15)

    override fun toString(): String
    {
        return tag.toTagString()
    }
}

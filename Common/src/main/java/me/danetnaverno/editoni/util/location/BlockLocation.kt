package me.danetnaverno.editoni.util.location

import me.danetnaverno.editoni.common.world.Chunk

data class BlockLocation(@JvmField val globalX: Int, @JvmField val globalY: Int, @JvmField val globalZ: Int)
{
    val localX: Int
        get() = globalX and 15
    val localY: Int
        get() = globalY
    val localZ: Int
        get() = globalZ and 15

    constructor(chunk: Chunk, localX: Int, localY: Int, localZ: Int)
            : this(chunk.location.x shl 4 or (localX and 15), localY, chunk.location.z shl 4 or (localZ and 15))

    fun add(x: Int, y: Int, z: Int): BlockLocation
    {
        return BlockLocation(this.globalX + x, this.globalY + y, this.globalZ + z)
    }

    fun toChunkLocation() : ChunkLocation
    {
        return ChunkLocation(globalX shr 4, globalZ shr 4)
    }
}
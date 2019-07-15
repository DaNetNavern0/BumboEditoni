package me.danetnaverno.editoni.util.location

import me.danetnaverno.editoni.common.world.Chunk

data class BlockLocation(@JvmField val globalX: Int, @JvmField val globalY: Int, @JvmField val globalZ: Int)
{
    @JvmField val localX: Int = globalX - (globalX shl 4)
    @JvmField val localY: Int = globalY
    @JvmField val localZ: Int = globalZ - (globalZ shl 4)

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
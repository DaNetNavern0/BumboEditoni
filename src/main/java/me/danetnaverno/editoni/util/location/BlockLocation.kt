package me.danetnaverno.editoni.util.location

import me.danetnaverno.editoni.world.Chunk

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

    fun add(location: BlockLocation): BlockLocation
    {
        return BlockLocation(this.globalX + location.globalX, this.globalY + location.localY, this.globalZ + location.localZ)
    }

    fun subtract(location: EntityLocation): EntityLocation
    {
        return EntityLocation(this.globalX - location.globalX, this.globalY - location.globalY, this.globalZ - location.globalZ)
    }

    fun toChunkLocation(): ChunkLocation
    {
        return ChunkLocation(globalX shr 4, globalZ shr 4)
    }

    override fun toString(): String
    {
        return "{$globalX, $globalY, $globalZ}"
    }

    fun toLocalString(): String
    {
        return "{$localX, $localY, $localZ}"
    }

    companion object
}
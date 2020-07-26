package me.danetnaverno.editoni.util.location

import me.danetnaverno.editoni.world.Chunk

open class BlockLocation
{
    var globalX: Int
        protected set
    var globalY: Int
        protected set
    var globalZ: Int
        protected set

    val localX: Int
        get() = globalX and 15
    val localY: Int
        get() = globalY
    val localZ: Int
        get() = globalZ and 15

    constructor(globalX: Int, globalY: Int, globalZ: Int)
    {
        this.globalX = globalX
        this.globalY = globalY
        this.globalZ = globalZ
    }

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

    override fun equals(other: Any?): Boolean
    {
        if (other is BlockLocation)
            return equals(other)
        return false
    }

    fun equals(other: BlockLocation): Boolean
    {
        return other.globalX == globalX && other.globalY == globalY && other.globalZ == globalZ
    }

    override fun hashCode(): Int
    {
        return (this.globalX * 31 + this.globalY) * 31 + this.globalZ
    }

    companion object

    class Mutable(globalX: Int, globalY: Int, globalZ: Int): BlockLocation(globalX, globalY, globalZ)
    {
        fun blockLocationFromSectionIndex(chunk: Chunk, section: Int, index: Int): Mutable
        {
            this.globalX = chunk.location.x shl 4 or ((index % 16) and 15)
            this.globalY = index / 256 + section * 16
            this.globalZ = chunk.location.z shl 4 or ((index % 256 / 16) and 15)
            return this;
        }

        fun addMutable(x: Int, y: Int, z: Int): Mutable
        {
            this.globalX += x
            this.globalY += y
            this.globalZ += z
            return this
        }
    }
}
package me.danetnaverno.editoni.location

import me.danetnaverno.editoni.render.ChunkRenderer
import me.danetnaverno.editoni.world.Chunk

open class BlockLocation : Cloneable
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
        return BlockLocation(globalX + x, globalY + y, globalZ + z)
    }

    fun add(location: BlockLocation): BlockLocation
    {
        return BlockLocation(globalX + location.globalX, globalY + location.localY, globalZ + location.localZ)
    }

    fun subtract(location: EntityLocation): EntityLocation
    {
        return EntityLocation(globalX - location.globalX, globalY - location.globalY, globalZ - location.globalZ)
    }

    open fun toChunkLocation(): ChunkLocation
    {
        return ChunkLocation(globalX shr 4, globalZ shr 4)
    }

    open fun toRegionLocation(): RegionLocation
    {
        return RegionLocation(globalX shr 10, globalZ shr 10)
    }

    open fun immutable(): BlockLocation
    {
        return this
    }

    fun isValid(): Boolean
    {
        return globalY in 0..255
    }

    //======================================================
    override fun toString(): String
    {
        return "{$globalX, $globalY, $globalZ}"
    }

    fun toLocalString(): String
    {
        return "{$localX, $localY, $localZ}"
    }

    public override fun clone(): BlockLocation
    {
        return BlockLocation(globalX, globalY, globalZ)
    }

    override fun hashCode(): Int
    {
        return (globalX * 31 + globalY) * 31 + globalZ
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

    companion object

    /**
     * When you need to use [BlockLocation] in large loops, this class can help to avoid rapid creation of massive
     * amounts of [BlockLocation]s just to be used once and then awaiting for the Garbage Collection.
     *
     * [toChunkLocation] creates a [ChunkLocation.Mutable] version of [ChunkLocation],
     * with the instance of [ChunkLocation.Mutable] being attached to the instance of [BlockLocation.Mutable]
     *
     * Same with [toRegionLocation]
     *
     * Usage example: [ChunkRenderer.updateVertices]
     *
     * @see [ChunkLocation]
     */
    class Mutable(globalX: Int, globalY: Int, globalZ: Int): BlockLocation(globalX, globalY, globalZ)
    {
        private val chunkLocation = ChunkLocation.Mutable(globalX shr 4, globalZ shr 4)

        override fun toChunkLocation(): ChunkLocation
        {
            return chunkLocation
        }

        override fun toRegionLocation(): RegionLocation
        {
            return chunkLocation.toRegionLocation()
        }

        override fun immutable(): BlockLocation
        {
            return BlockLocation(globalX, globalY, globalZ)
        }

        override fun clone(): Mutable
        {
            return Mutable(globalX, globalY, globalZ)
        }

        fun blockLocationFromSectionIndex(chunk: Chunk, section: Int, index: Int): Mutable
        {
            globalX = chunk.location.x shl 4 or ((index % 16) and 15)
            globalY = index / 256 + section * 16
            globalZ = chunk.location.z shl 4 or ((index % 256 / 16) and 15)
            chunkLocation.setMutably(globalX shr 4, globalZ shr 4)
            return this
        }

        fun addMutably(x: Int, y: Int, z: Int): Mutable
        {
            globalX += x
            globalY += y
            globalZ += z
            chunkLocation.setMutably(globalX shr 4, globalZ shr 4)
            return this
        }

        fun setMutably(x: Int, y: Int, z: Int): Mutable
        {
            globalX = x
            globalY = y
            globalZ = z
            chunkLocation.setMutably(globalX shr 4, globalZ shr 4)
            return this
        }
    }
}
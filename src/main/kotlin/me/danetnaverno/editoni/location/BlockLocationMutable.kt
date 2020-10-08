package me.danetnaverno.editoni.location

import me.danetnaverno.editoni.render.ChunkRenderer
import me.danetnaverno.editoni.world.Chunk

/**
 * When you need to use [BlockLocation] in large loops, this class can help to avoid rapid creation of massive
 * amounts of [BlockLocation]s just to be used once and then left waiting for the Garbage Collection.
 *
 * [toChunkLocation] creates a [ChunkLocationMutable] instead of [ChunkLocation],
 * with the instance of [ChunkLocationMutable] being attached to the instance of [BlockLocationMutable]
 *
 * Same with [toRegionLocation]
 *
 * Usage example: [ChunkRenderer.updateVertices]
 *
 * @see [ChunkLocation]
 */
class BlockLocationMutable(globalX: Int, globalY: Int, globalZ: Int) : IBlockLocation
{
    override var globalX: Int = globalX
        private set
    override var globalY: Int = globalY
        private set
    override var globalZ: Int = globalZ
        private set

    private val chunkLocation = ChunkLocationMutable(globalX shr 4, globalZ shr 4)

    override fun toChunkLocation(): ChunkLocationMutable
    {
        return chunkLocation
    }

    override fun toRegionLocation(): RegionLocationMutable
    {
        return chunkLocation.toRegionLocation()
    }

    fun blockLocationFromSectionIndex(chunk: Chunk, section: Int, index: Int): BlockLocationMutable
    {
        globalX = chunk.chunkLocation.x shl 4 or ((index % 16) and 15)
        globalY = index / 256 + section * 16
        globalZ = chunk.chunkLocation.z shl 4 or ((index % 256 / 16) and 15)
        chunkLocation.set(globalX shr 4, globalZ shr 4)
        return this
    }

    fun add(x: Int, y: Int, z: Int): BlockLocationMutable
    {
        globalX += x
        globalY += y
        globalZ += z
        chunkLocation.set(globalX shr 4, globalZ shr 4)
        return this
    }

    fun set(x: Int, y: Int, z: Int): BlockLocationMutable
    {
        globalX = x
        globalY = y
        globalZ = z
        chunkLocation.set(globalX shr 4, globalZ shr 4)
        return this
    }

    //===============================================================================================
    // Yes, this is some hacky stuff right there.
    // By doing this, we make it possible to check if a collection has a BlockLocation,
    //   when we check collection#contains with a BlockLocationMutable.
    // For an example, look at World#getRegion.
    // It's up to a programmer to make sure Mutable Locations are never placed into collections.
    //===============================================================================================
    override fun hashCode(): Int
    {
        return (globalX * 31 + globalY) * 31 + globalZ
    }

    override fun equals(other: Any?): Boolean
    {
        return other is IBlockLocation && other.globalX == globalX && other.globalY == globalY && globalZ == globalZ
    }

    override fun toString(): String
    {
        return "BlockLocationMutable(Global: $globalX, $globalY, $globalZ; Local: $localX, $localY, $localZ)"
    }
}
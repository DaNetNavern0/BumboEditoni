package me.danetnaverno.editoni.location

import kotlin.math.sqrt

data class ChunkLocation(override val x: Int, override val z: Int) : IChunkLocation
{
    override fun toRegionLocation(): RegionLocation
    {
        return RegionLocation(x shr 6, z shr 6)
    }

    fun add(x: Int, z: Int): ChunkLocation
    {
        return ChunkLocation(this.x + x, this.z + z)
    }

    fun add(location: ChunkLocation): ChunkLocation
    {
        return ChunkLocation(this.x + location.x, this.z + location.z)
    }

    fun subtract(location: ChunkLocation): ChunkLocation
    {
        return ChunkLocation(this.x - location.x, this.z - location.z)
    }

    fun distance(other: ChunkLocation): Double
    {
        return sqrt(distanceSquared(other).toDouble())
    }

    fun distanceSquared(other: ChunkLocation): Int
    {
        val dx = this.x - other.x
        val dz = this.z - other.z
        return dx * dx + dz * dz
    }

    //========================================
    // Yes, this is some hacky stuff right there.
    // By doing this, we make it possible to check if a collection has a ChunkLocation,
    // if we check collection#contains with a ChunkLocationMutable.
    // For an example, look at World#getRegion
    // It's up to a programmer to make sure Mutable Locations are never placed into collections.
    //========================================
    override fun hashCode(): Int
    {
        return x * 31 + z
    }

    override fun equals(other: Any?): Boolean
    {
        return other is IChunkLocation && other.x == x && other.z == z
    }
}
package me.danetnaverno.editoni.location

/**
 * @see [BlockLocationMutable]
 */
class ChunkLocationMutable(x: Int, z: Int) : IChunkLocation
{
    override var x: Int = x
        private set
    override var z: Int = z
        private set

    private val regionLocation = RegionLocationMutable(x shr 5, z shr 5)

    override fun toRegionLocation(): RegionLocationMutable
    {
        return regionLocation
    }

    fun add(x: Int, z: Int): ChunkLocationMutable
    {
        this.x += x
        this.z += z
        regionLocation.set(this.x shr 5, this.z shr 5)
        return this
    }

    fun set(x: Int, z: Int): ChunkLocationMutable
    {
        this.x = x
        this.z = z
        regionLocation.set(x shr 5, z shr 5)
        return this
    }

    //========================================
    // Yes, this is some hacky stuff right there.
    // By doing this, we make it possible to check if a collection has a ChunkLocation,
    //   when we check collection#contains with a ChunkLocationMutable.
    // For an example, look at World#getRegion.
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

    override fun toString(): String
    {
        return "ChunkLocationMutable($x, $z)"
    }
}
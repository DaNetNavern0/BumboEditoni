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

    private val regionLocation = RegionLocationMutable(x shr 6, z shr 6)

    override fun toRegionLocation(): RegionLocationMutable
    {
        return regionLocation
    }

    fun add(x: Int, z: Int): ChunkLocationMutable
    {
        this.x += x
        this.z += z
        regionLocation.set(this.x shr 6, this.z shr 6)
        return this
    }

    fun set(x: Int, z: Int): ChunkLocationMutable
    {
        this.x = x
        this.z = z
        regionLocation.set(x shr 6, z shr 6)
        return this
    }

    //===================
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
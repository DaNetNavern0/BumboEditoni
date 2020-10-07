package me.danetnaverno.editoni.location

data class ChunkLocation(override val x: Int, override val z: Int) : IChunkLocation
{
    override fun toRegionLocation(): RegionLocation
    {
        return RegionLocation(x shr 5, z shr 5)
    }

    fun add(x: Int, z: Int): ChunkLocation
    {
        return ChunkLocation(this.x + x, this.z + z)
    }

    fun add(location: IChunkLocation): ChunkLocation
    {
        return ChunkLocation(this.x + location.x, this.z + location.z)
    }

    fun subtract(location: IChunkLocation): ChunkLocation
    {
        return ChunkLocation(this.x - location.x, this.z - location.z)
    }

    //===============================================================================================
    // Yes, this is some hacky stuff right there.
    // By doing this, we make it possible to check if a collection has a ChunkLocation,
    //   when we check collection#contains with a ChunkLocationMutable.
    // For an example, look at World#getRegion
    // It's up to a programmer to make sure Mutable Locations are never placed into collections.
    //===============================================================================================
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
        return "ChunkLocation($x, $z)"
    }
}
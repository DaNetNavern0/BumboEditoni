package me.danetnaverno.editoni.location

import me.danetnaverno.editoni.world.Chunk

data class BlockLocation(override val globalX: Int, override val globalY: Int, override val globalZ: Int) : IBlockLocation
{
    constructor(chunk: Chunk, localX: Int, localY: Int, localZ: Int)
            : this(chunk.location.x shl 4 or (localX and 15), localY, chunk.location.z shl 4 or (localZ and 15))

    fun add(x: Int, y: Int, z: Int): BlockLocation
    {
        return BlockLocation(globalX + x, globalY + y, globalZ + z)
    }

    fun add(location: IBlockLocation): BlockLocation
    {
        return BlockLocation(globalX + location.globalX, globalY + location.localY, globalZ + location.localZ)
    }

    fun subtract(location: IBlockLocation): BlockLocation
    {
        return BlockLocation(globalX - location.globalX, globalY - location.localY, globalZ - location.localZ)
    }

    override fun toChunkLocation(): ChunkLocation
    {
        return ChunkLocation(globalX shr 4, globalZ shr 4)
    }

    override fun toRegionLocation(): RegionLocation
    {
        return RegionLocation(globalX shr 9, globalZ shr 9)
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
        return "BlockLocation(Global: $globalX, $globalY, $globalZ; Local: $localX, $localY, $localZ)"
    }

    companion object
}
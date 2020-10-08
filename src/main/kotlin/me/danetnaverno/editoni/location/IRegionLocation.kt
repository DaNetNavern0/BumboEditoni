package me.danetnaverno.editoni.location

interface IRegionLocation
{
    val x: Int
    val z: Int

    fun toImmutable(): RegionLocation
    {
        return RegionLocation(x, z)
    }

    fun isChunkLocationBelongs(chunkLocation: IChunkLocation): Boolean
    {
        return chunkLocation.x shr 5 == x && chunkLocation.z shr 5 == z
    }
}
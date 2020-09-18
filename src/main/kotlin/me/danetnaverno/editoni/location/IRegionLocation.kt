package me.danetnaverno.editoni.location

interface IRegionLocation
{
    val x: Int
    val z: Int

    fun toImmutable(): RegionLocation
    {
        return RegionLocation(x, z)
    }

    fun isChunkLocationBelongs(location: IChunkLocation): Boolean
    {
        return location.x shr 6 == x && location.z shr 6 == z
    }
}
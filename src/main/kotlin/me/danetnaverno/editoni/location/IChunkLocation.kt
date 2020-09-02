package me.danetnaverno.editoni.location

interface IChunkLocation
{
    val x: Int
    val z: Int

    fun toRegionLocation(): IRegionLocation

    fun toImmutable(): ChunkLocation
    {
        return ChunkLocation(x, z)
    }

    fun isBlockLocationBelongs(pos: IBlockLocation): Boolean
    {
        return pos.globalX shr 4 == x && pos.globalZ shr 4 == z
    }
}
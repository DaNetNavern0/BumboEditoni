package me.danetnaverno.editoni.location

interface IBlockLocation
{
    val globalX: Int
    val globalY: Int
    val globalZ: Int

    val localX: Int
        get() = globalX and 15
    val localY: Int
        get() = globalY
    val localZ: Int
        get() = globalZ and 15

    fun toChunkLocation(): IChunkLocation
    fun toRegionLocation(): IRegionLocation

    fun toImmutable(): BlockLocation
    {
        return BlockLocation(globalX, globalY, globalZ)
    }

    fun isValid(): Boolean
    {
        return globalY in 0..255
    }

    fun toSectionBlockIndex(): Int
    {
        return (localY % 16) * 256 + localZ * 16 + localX
    }

    fun toChunkBlockIndex(): Int
    {
        return localY * 256 + localZ * 16 + localX
    }
}
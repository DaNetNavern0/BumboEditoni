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
        return (globalY % 16) * 256 + (globalZ and 15) * 16 + (globalX and 15)
    }

    fun toChunkBlockIndex(): Int
    {
        return globalY * 256 + (globalZ and 15) * 16 + (globalX and 15)
    }
}
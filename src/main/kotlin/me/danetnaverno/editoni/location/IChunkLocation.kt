package me.danetnaverno.editoni.location

import kotlin.math.abs
import kotlin.math.sqrt

interface IChunkLocation
{
    val x: Int
    val z: Int

    fun toRegionLocation(): IRegionLocation

    fun toImmutable(): ChunkLocation
    {
        return ChunkLocation(x, z)
    }

    fun distance(other: IChunkLocation): Double
    {
        return sqrt(distanceSquared(other).toDouble())
    }

    fun withinCubicDistance(other: IChunkLocation, distance: Int): Boolean
    {
        return abs(x - other.x) <= distance && abs(z - other.z) <= distance
    }

    fun distanceSquared(other: IChunkLocation): Int
    {
        val dx = this.x - other.x
        val dz = this.z - other.z
        return dx * dx + dz * dz
    }

    fun isBlockLocationBelongs(pos: IBlockLocation): Boolean
    {
        return pos.globalX shr 4 == x && pos.globalZ shr 4 == z
    }
}
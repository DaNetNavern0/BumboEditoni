package me.danetnaverno.editoni.util.location

import java.lang.Math.sqrt

data class ChunkLocation(@JvmField val x: Int, @JvmField val z: Int)
{
    fun isBlockLocationBelongs(pos: BlockLocation): Boolean
    {
        return pos.globalX shr 4 == x && pos.globalZ shr 4 == z
    }

    fun add(x: Int, z: Int): ChunkLocation
    {
        return ChunkLocation(this.x + x, this.z + z)
    }

    fun add(location: ChunkLocation): ChunkLocation
    {
        return ChunkLocation(this.x + location.x, this.z + location.z)
    }

    fun subtract(location: ChunkLocation): ChunkLocation
    {
        return ChunkLocation(this.x - location.x, this.z - location.z)
    }

    fun distance(other: ChunkLocation): Double
    {
        return sqrt(distanceSquared(other).toDouble())
    }

    fun distanceSquared(other: ChunkLocation): Int
    {
        val dx = this.x - other.x
        val dz = this.z - other.z
        return dx * dx + dz * dz
    }
}
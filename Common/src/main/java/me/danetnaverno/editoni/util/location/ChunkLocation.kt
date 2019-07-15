package me.danetnaverno.editoni.util.location

import org.joml.Math

data class ChunkLocation(@JvmField val x: Int, @JvmField val z: Int)
{
    fun isBlockLocationBelongs(pos: BlockLocation) : Boolean
    {
        return pos.toChunkLocation() == this
    }

    fun distance(other: ChunkLocation): Double
    {
        return Math.sqrt(distanceSquared(other).toDouble())
    }

    fun distanceSquared(other: ChunkLocation): Int
    {
        val dx = this.x - other.x
        val dz = this.z - other.z
        return dx * dx + dz * dz
    }
}
package me.danetnaverno.editoni.world.util.location

import org.joml.Math

data class RegionLocation(@JvmField val x: Int, @JvmField val z: Int)
{
    fun distance(other: RegionLocation): Double
    {
        return Math.sqrt(distanceSquared(other).toDouble())
    }

    fun distanceSquared(other: RegionLocation): Int
    {
        val dx = this.x - other.x
        val dz = this.z - other.z
        return dx * dx + dz * dz
    }
}
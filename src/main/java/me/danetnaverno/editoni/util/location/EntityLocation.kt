package me.danetnaverno.editoni.util.location

import org.joml.Math

data class EntityLocation(@JvmField val globalX: Double, @JvmField val globalY: Double, @JvmField val globalZ: Double)
{
    fun add(x: Double, y: Double, z: Double): EntityLocation
    {
        return EntityLocation(this.globalX + x, this.globalY + y, this.globalZ + z)
    }

    fun add(location: EntityLocation): EntityLocation
    {
        return EntityLocation(this.globalX + location.globalX, this.globalY + location.globalY, this.globalZ + location.globalZ)
    }

    fun subtract(location: EntityLocation): EntityLocation
    {
        return EntityLocation(this.globalX - location.globalX, this.globalY - location.globalY, this.globalZ - location.globalZ)
    }

    fun toBlockLocation(): BlockLocation
    {
        return BlockLocation(globalX.toInt(), globalY.toInt(), globalZ.toInt())
    }

    fun toChunkLocation(): ChunkLocation
    {
        return toBlockLocation().toChunkLocation()
    }

    fun distance(other: EntityLocation): Double
    {
        return Math.sqrt(distanceSquared(other));
    }

    fun distanceSquared(other: EntityLocation): Double
    {
        val dx = this.globalX - other.globalX
        val dy = this.globalY - other.globalY
        val dz = this.globalZ - other.globalZ
        return dx * dx + dy * dy + dz * dz
    }
}
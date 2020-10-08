package me.danetnaverno.editoni.location

import kotlin.math.sqrt

data class EntityLocation(val globalX: Double, val globalY: Double, val globalZ: Double)
{
    fun add(x: Double, y: Double, z: Double): EntityLocation
    {
        return EntityLocation(this.globalX + x, this.globalY + y, this.globalZ + z)
    }

    fun add(entityLocation: EntityLocation): EntityLocation
    {
        return EntityLocation(this.globalX + entityLocation.globalX, this.globalY + entityLocation.globalY, this.globalZ + entityLocation.globalZ)
    }

    fun subtract(entityLocation: EntityLocation): EntityLocation
    {
        return EntityLocation(this.globalX - entityLocation.globalX, this.globalY - entityLocation.globalY, this.globalZ - entityLocation.globalZ)
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
        return sqrt(distanceSquared(other))
    }

    fun distanceSquared(other: EntityLocation): Double
    {
        val dx = this.globalX - other.globalX
        val dy = this.globalY - other.globalY
        val dz = this.globalZ - other.globalZ
        return dx * dx + dy * dy + dz * dz
    }
}
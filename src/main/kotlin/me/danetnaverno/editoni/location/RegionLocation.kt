package me.danetnaverno.editoni.location

import kotlin.math.sqrt


data class RegionLocation(override val x: Int, override val z: Int) : IRegionLocation
{
    fun distance(other: RegionLocation): Double
    {
        return sqrt(distanceSquared(other).toDouble())
    }

    fun distanceSquared(other: RegionLocation): Int
    {
        val dx = this.x - other.x
        val dz = this.z - other.z
        return dx * dx + dz * dz
    }

    //===============================================================================================
    // Yes, this is some hacky stuff right there.
    // By doing this, we make it possible to check if a collection has a RegionLocation,
    //   when we check collection#contains with a RegionLocationMutable.
    // For an example, look at World#getRegion.
    // It's up to a programmer to make sure Mutable Locations are never placed into collections.
    //===============================================================================================
    override fun hashCode(): Int
    {
        return x * 31 + z
    }

    override fun equals(other: Any?): Boolean
    {
        return other is IRegionLocation && other.x == x && other.z == z
    }

    override fun toString(): String
    {
        return "RegionLocation($x, $z)"
    }
}
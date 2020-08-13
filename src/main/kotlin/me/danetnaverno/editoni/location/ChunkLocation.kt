package me.danetnaverno.editoni.location

import kotlin.math.sqrt

open class ChunkLocation(x: Int, z: Int) : Cloneable
{
    var x: Int = x
        protected set
    var z: Int = z
        protected set

    open fun toRegionLocation(): RegionLocation
    {
        return RegionLocation(x shr 6, z shr 6)
    }

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

    open fun immutable(): ChunkLocation
    {
        return this
    }

    //======================================================
    override fun toString(): String
    {
        return "{$x, $z}"
    }

    public override fun clone(): ChunkLocation
    {
        return ChunkLocation(x, z)
    }

    override fun hashCode(): Int
    {
        return x * 31 + z
    }

    override fun equals(other: Any?): Boolean
    {
        if (other is ChunkLocation)
            return equals(other)
        return false
    }

    fun equals(other: ChunkLocation): Boolean
    {
        return other.x == x && other.z == z
    }

    class Mutable(x: Int, z: Int): ChunkLocation(x, z)
    {
        private val regionLocation = RegionLocation.Mutable(x shr 6, z shr 6)

        override fun toRegionLocation(): RegionLocation
        {
            return regionLocation
        }

        override fun immutable(): ChunkLocation
        {
            return ChunkLocation(x, z)
        }

        override fun clone(): Mutable
        {
            return Mutable(x, z)
        }

        fun setMutably(x: Int, z: Int)
        {
            this.x = x
            this.z = z
            regionLocation.setMutably(x shr 6, z shr 6)
        }
    }
}
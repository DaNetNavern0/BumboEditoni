package me.danetnaverno.editoni.location


open class RegionLocation(x: Int, z: Int) : Cloneable
{
    var x: Int = x
        protected set
    var z: Int = z
        protected set

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

    open fun immutable(): RegionLocation
    {
        return this
    }

    //======================================================
    override fun toString(): String
    {
        return "{$x, $z}"
    }

    public override fun clone(): RegionLocation
    {
        return RegionLocation(x, z)
    }

    override fun hashCode(): Int
    {
        return x * 31 + z
    }

    override fun equals(other: Any?): Boolean
    {
        if (other is RegionLocation)
            return equals(other)
        return false
    }

    fun equals(other: RegionLocation): Boolean
    {
        return other.x == x && other.z == z
    }

    /**
     * @see [BlockLocation.Mutable]
     */
    class Mutable(x: Int, z: Int) : RegionLocation(x, z)
    {
        override fun immutable(): RegionLocation
        {
            return RegionLocation(x, z)
        }

        override fun clone(): Mutable
        {
            return Mutable(x, z)
        }

        fun addMutably(x: Int, z: Int): Mutable
        {
            this.x += x
            this.z += z
            return this
        }

        fun setMutably(x: Int, z: Int): Mutable
        {
            this.x = x
            this.z = z
            return this
        }
    }
}
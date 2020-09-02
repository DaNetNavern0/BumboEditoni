package me.danetnaverno.editoni.location

/**
 * @see [BlockLocationMutable]
 */
class RegionLocationMutable(x: Int, z: Int) : IRegionLocation
{
    override var x: Int = x
        private set
    override var z: Int = z
        private set

    fun add(x: Int, z: Int): RegionLocationMutable
    {
        this.x += x
        this.z += z
        return this
    }

    fun set(x: Int, z: Int): RegionLocationMutable
    {
        this.x = x
        this.z = z
        return this
    }

    //========================================
    // Yes, this is some hacky stuff right there.
    // By doing this, we make it possible to check if a collection has a RegionLocation,
    // if we check collection#contains with a RegionLocationMutable.
    // For an example, look at World#getRegion.
    // It's up to a programmer to make sure Mutable Locations are never placed into collections.
    //========================================
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
        return "RegionLocationMutable($x, $z)"
    }
}
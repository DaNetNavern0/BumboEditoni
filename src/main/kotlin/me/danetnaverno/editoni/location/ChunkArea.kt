package me.danetnaverno.editoni.location

import me.danetnaverno.editoni.world.World

/**
 * This class represents an area of chunks. Or rather, [ChunkLocation]s.
 *
 * It does not hold the chunk objects themselves, but you can get an iterator that will iterate over [ChunkLocation]s
 */
class ChunkArea(val world: World, cornerA: ChunkLocation, cornerB: ChunkLocation) : Iterable<ChunkLocation>
{
    val min = ChunkLocation(
            Math.min(cornerA.x, cornerB.x),
            Math.min(cornerA.z, cornerB.z))

    val max = ChunkLocation(
            Math.max(cornerA.x, cornerB.x),
            Math.max(cornerA.z, cornerB.z))

    override fun iterator(): Iterator
    {
        return Iterator(min, max)
    }

    class Iterator(min: ChunkLocation, max: ChunkLocation) : kotlin.collections.Iterator<ChunkLocation>
    {
        private val minX = min.x

        private val maxX = max.x
        private val maxZ = max.z

        private var currentX = min.x - 1
        private var currentZ = min.z

        override fun hasNext(): Boolean
        {
            return !(currentX >= maxX && currentZ >= maxZ)
        }

        override fun next(): ChunkLocation
        {
            currentX++
            if (currentX > maxX)
            {
                currentX = minX
                currentZ++
            }
            return ChunkLocation(currentX, currentZ)
        }
    }
}

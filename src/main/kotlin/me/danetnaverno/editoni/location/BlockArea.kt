package me.danetnaverno.editoni.location

import me.danetnaverno.editoni.world.World

class BlockArea(val world: World, cornerA: BlockLocation, cornerB: BlockLocation)
{
    val min = BlockLocation(
            Math.min(cornerA.globalX, cornerB.globalX),
            Math.min(cornerA.globalY, cornerB.globalY),
            Math.min(cornerA.globalZ, cornerB.globalZ))

    val max = BlockLocation(
            Math.max(cornerA.globalX, cornerB.globalX),
            Math.max(cornerA.globalY, cornerB.globalY),
            Math.max(cornerA.globalZ, cornerB.globalZ))

    fun toChunkArea() : ChunkArea
    {
        return ChunkArea(world, min.toChunkLocation(), max.toChunkLocation())
    }

    /**
     * More taxing than [mutableIterator], since it creates a new BlockLocation object each iteration, but it may be needed in some cases
     */
    fun immutableIterator(): Iterator<BlockLocation>
    {
        return BlockAreaImmutableIterator(min, max)
    }

    /**
     * This iterator reuses the same [BlockLocation.Mutable] object through iteration for the optimization sake
     *
     * To consider: do we actually need this, since in many occasions we need an immutable version of this anyway?
     * Look at the usages of [BlockLocation.immutable]
     */
    fun mutableIterator(): Iterator<BlockLocation>
    {
        return BlockAreaMutableIterator(min, max)
    }

    override fun toString(): String
    {
        return "BlockArea{$min...$max}"
    }
}

abstract class BlockAreaIterator(min: BlockLocation, max: BlockLocation) : Iterator<BlockLocation>
{
    protected val minX = min.globalX
    protected val minY = min.globalY

    protected val maxX = max.globalX
    protected val maxY = max.globalY
    protected val maxZ = max.globalZ

    protected var currentX = min.globalX - 1
    protected var currentY = min.globalY
    protected var currentZ = min.globalZ

    override fun hasNext(): Boolean
    {
        return !(currentX >= maxX && currentY >= maxY && currentZ >= maxZ)
    }

    fun shiftIndex()
    {
        currentX++
        if (currentX > maxX)
        {
            currentX = minX
            currentY++
            if (currentY > maxY)
            {
                currentY = minY
                currentZ++
            }
        }
    }
}

class BlockAreaImmutableIterator(min: BlockLocation, max: BlockLocation) : BlockAreaIterator(min, max)
{
    override fun next(): BlockLocation
    {
        shiftIndex()
        return BlockLocation(currentX, currentY, currentZ)
    }
}

class BlockAreaMutableIterator(min: BlockLocation, max: BlockLocation) : BlockAreaIterator(min, max)
{
    private val currentLocation = BlockLocation.Mutable(currentX, currentY, currentZ)
    override fun next(): BlockLocation
    {
        shiftIndex()
        return currentLocation.setMutably(currentX, currentY, currentZ)
    }
}

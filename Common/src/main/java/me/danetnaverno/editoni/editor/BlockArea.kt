package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.util.location.BlockLocation

class BlockArea(val world: World, cornerA: BlockLocation, cornerB: BlockLocation) : Iterable<BlockLocation>
{
    val min: BlockLocation = BlockLocation(
            Math.min(cornerA.globalX, cornerB.globalX),
            Math.min(cornerA.globalY, cornerB.globalY),
            Math.min(cornerA.globalZ, cornerB.globalZ))

    val max: BlockLocation = BlockLocation(
            Math.max(cornerA.globalX, cornerB.globalX),
            Math.max(cornerA.globalY, cornerB.globalY),
            Math.max(cornerA.globalZ, cornerB.globalZ))

    fun getLocations(): Collection<BlockLocation>
    {
        val result = arrayListOf<BlockLocation>()
        for (x in min.globalX..max.globalX)
            for (y in min.globalY..max.globalY)
                for (z in min.globalZ..max.globalZ)
                    result.add(BlockLocation(x, y, z))
        return result
    }

    override fun iterator(): Iterator<BlockLocation>
    {
        return getLocations().iterator()
    }
}
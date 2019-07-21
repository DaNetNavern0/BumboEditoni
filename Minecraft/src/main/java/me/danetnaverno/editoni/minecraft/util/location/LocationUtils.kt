package me.danetnaverno.editoni.minecraft.util.location

import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation

fun BlockLocation.toSectionBlockIndex() : Int
{
    return (localY % 16) * 256 + localZ * 16 + localX
}

fun BlockLocation.toChunkBlockIndex() : Int
{
    return localY * 256 + localZ * 16 + localX
}

fun BlockLocation.Companion.fromBlockIndex(chunk: Chunk, index: Int) : BlockLocation
{
    return BlockLocation(chunk, index % 16, index / 256, index % 256 / 16)
}

fun BlockLocation.Companion.fromSectionIndex(chunk: Chunk, section: Int, index: Int) : BlockLocation
{
    return BlockLocation(chunk, index % 16, index / 256 + section * 16, index % 256 / 16)
}

fun ChunkLocation.toRegionLocation() : RegionLocation
{
    return RegionLocation(x shr 6, z shr 6)
}
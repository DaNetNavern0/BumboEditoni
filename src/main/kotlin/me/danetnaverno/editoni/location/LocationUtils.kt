package me.danetnaverno.editoni.location

import me.danetnaverno.editoni.world.Chunk

fun BlockLocation.getSection(): Int
{
    return localY / 16
}

fun BlockLocation.Companion.fromBlockIndex(chunk: Chunk, index: Int): BlockLocation
{
    return BlockLocation(chunk, index % 16, index / 256, index % 256 / 16)
}

fun blockLocationFromSectionIndex(chunk: Chunk, section: Int, index: Int): BlockLocation
{
    return BlockLocation(chunk, index % 16, index / 256 + section * 16, index % 256 / 16)
}

fun ChunkLocation.toRegionOffset(): ChunkRegionOffset
{
    val regionLocation = this.toRegionLocation()
    return ChunkRegionOffset(regionLocation.x * 32 + x, regionLocation.z * 32 + z)
}

fun ChunkRegionOffset.toChunkLocation(regionLocation: RegionLocation): ChunkLocation
{
    return ChunkLocation(x - regionLocation.x * 32, z - regionLocation.z * 32)
}
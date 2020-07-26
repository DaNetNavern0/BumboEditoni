package me.danetnaverno.editoni.util.location

import me.danetnaverno.editoni.world.Chunk

fun BlockLocation.toSectionBlockIndex(): Int
{
    return (localY % 16) * 256 + localZ * 16 + localX
}

fun BlockLocation.getSection(): Int
{
    return localY / 16
}

fun BlockLocation.toChunkBlockIndex(): Int
{
    return localY * 256 + localZ * 16 + localX
}

fun BlockLocation.Companion.fromBlockIndex(chunk: Chunk, index: Int): BlockLocation
{
    return BlockLocation(chunk, index % 16, index / 256, index % 256 / 16)
}

fun blockLocationFromSectionIndex(chunk: Chunk, section: Int, index: Int): BlockLocation
{
    return BlockLocation(chunk, index % 16, index / 256 + section * 16, index % 256 / 16)
}

fun ChunkLocation.toRegionLocation(): RegionLocation
{
    return RegionLocation(x shr 6, z shr 6)
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
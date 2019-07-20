package me.danetnaverno.editoni.minecraft.utils

import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.util.location.BlockLocation

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
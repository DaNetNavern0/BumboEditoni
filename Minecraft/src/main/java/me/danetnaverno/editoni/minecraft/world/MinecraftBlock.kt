package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.minecraft.utils.fromBlockIndex
import me.danetnaverno.editoni.minecraft.utils.toChunkBlockIndex
import me.danetnaverno.editoni.util.location.BlockLocation

class MinecraftBlock(chunk: Chunk, location: BlockLocation, blockType: BlockType) : Block(chunk, location, blockType)
{
    private val locIndex = location.toChunkBlockIndex()
    override val location
        get() = BlockLocation.fromBlockIndex(chunk, locIndex)
}
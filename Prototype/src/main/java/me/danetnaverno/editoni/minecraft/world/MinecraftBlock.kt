package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.block.BlockType
import me.danetnaverno.editoni.common.world.Block

class MinecraftBlock
(chunk: MinecraftChunk, chunkX: Int, chunkY: Int, chunkz: Int, blockType: BlockType, blockState: MinecraftBlockState?, tileEntity: MinecraftTileEntity?)
    : Block(chunk, chunkX, chunkY, chunkz, blockType, blockState, tileEntity)
{
}
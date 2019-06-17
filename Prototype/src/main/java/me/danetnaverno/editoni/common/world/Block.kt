package me.danetnaverno.editoni.common.world

import me.danetnaverno.editoni.common.block.BlockType

abstract class Block(val chunk: Chunk, val chunkX: Int, val chunkY: Int, val chunkZ: Int,
                     val type: BlockType, val state: BlockState?, val tileEntity: TileEntity?)
{
    val globalX: Int
        get() = chunk.xPos shl 4 or (chunkX and 15)

    val globalY: Int
        get() = chunkY

    val globalZ: Int
        get() = chunk.zPos shl 4 or (chunkZ and 15)
}
package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.blockstate.BlockState
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.location.BlockLocation

/**
 * Blocks in the world aren't actually stored with this class.
 * [Block] instances get creates when you need to store data about specific blocks, e.g. in the operation history
 * todo Maybe it makes sense to create a BlockArray class, that would store blocks similar to [Chunk], or even Minecraft's method of storing blocks
 */
data class Block(val chunk: Chunk, val location: BlockLocation, val type: BlockType, val state: BlockState?, val tileEntity: TileEntity?)
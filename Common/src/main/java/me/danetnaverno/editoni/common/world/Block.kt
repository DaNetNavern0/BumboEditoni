package me.danetnaverno.editoni.common.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.util.location.BlockLocation

/**
 * Each instance of [Block] represents a particular block with its coords in a world.
 *
 * [Block] instance and its fields aren't supposed to be edited directly,
 * but instead you're supposed to create a new [Block] instance with new data,
 * and create a new [Operation] to apply it to a world.
 *
 * If you want to have a "coord-free block info", you can just ignore coords and apply them before making an [Operation]
 */
open class Block(open val chunk: Chunk, open val location: BlockLocation,
                     open val type: BlockType, open val state: BlockState?, open val tileEntity: TileEntity?)
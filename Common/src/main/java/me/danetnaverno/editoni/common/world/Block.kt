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
abstract class Block(val chunk: Chunk, @JvmField val location: BlockLocation,
                     val type: BlockType, val state: BlockState?, val tileEntity: TileEntity?)
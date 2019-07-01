package me.danetnaverno.editoni.common.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.editor.operations.Operation
import me.danetnaverno.editoni.util.globalToLocalPos
import org.joml.Vector3i

/**
 * Each instance of [Block] represents a particular block with its coords in a world.
 *
 * [Block] instance and its fields aren't supposed to be edited directly,
 * but instead you're supposed to create a new [Block] instance with new data,
 * and create a new [Operation] to apply it to a world.
 *
 * If you want to have a "coord-free block info", you can just ignore coords and apply them before making an [Operation]
 */
abstract class Block(val chunk: Chunk, val localPos: Vector3i,
                     val type: BlockType, val state: BlockState?, val tileEntity: TileEntity?)
{
    constructor(world: World, globalPos: Vector3i, blockType: BlockType, blockState: BlockState?, tileEntity: TileEntity?)
            : this(world.getChunkByBlockCoord(globalPos.x, globalPos.z), globalToLocalPos(globalPos), blockType, blockState, tileEntity)

    val globalX: Int
        get() = chunk.xPos shl 4 or (localPos.x and 15)

    val globalY: Int
        get() = localPos.y

    val globalZ: Int
        get() = chunk.zPos shl 4 or (localPos.z and 15)

    val globalPos: Vector3i
        get() = Vector3i(globalX, globalY, globalZ)
}
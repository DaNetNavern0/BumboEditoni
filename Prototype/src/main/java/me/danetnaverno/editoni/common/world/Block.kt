package me.danetnaverno.editoni.common.world

import me.danetnaverno.editoni.common.block.BlockType
import org.joml.Vector3i

abstract class Block(val chunk: Chunk, val localPos: Vector3i,
                     val type: BlockType, val state: BlockState?, val tileEntity: TileEntity?)
{
    val globalX: Int
        get() = chunk.xPos shl 4 or (localPos.x and 15)

    val globalY: Int
        get() = localPos.y

    val globalZ: Int
        get() = chunk.zPos shl 4 or (localPos.z and 15)

    val globalPos: Vector3i
        get() = Vector3i(globalX, globalY, globalZ)
}
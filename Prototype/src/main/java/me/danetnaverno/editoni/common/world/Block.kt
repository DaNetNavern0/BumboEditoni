package me.danetnaverno.editoni.common.world

abstract class Block(val chunk: Chunk, val localX: Int, val localY: Int, val localZ: Int)
{
    lateinit var id: String
    lateinit var blockState: BlockState
    var data: BlockNBT? = null

    val globalX: Int
        get() = chunk.xPos shl 4 or (localX and 15)

    val globalY: Int
        get() = localY

    val globalZ: Int
        get() = chunk.zPos shl 4 or (localZ and 15)
}
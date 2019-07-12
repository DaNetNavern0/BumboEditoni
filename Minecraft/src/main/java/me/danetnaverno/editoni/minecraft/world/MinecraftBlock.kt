package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.common.world.*
import me.danetnaverno.editoni.util.globalToLocalPos
import org.joml.Vector3i

class MinecraftBlock(chunk: Chunk, localPos: Vector3i, blockType: BlockType, blockState: BlockState?, tileEntity: TileEntity?)
    : Block(chunk, localPos, blockType, blockState, tileEntity)
{
    constructor(world: World, globalPos: Vector3i, blockType: BlockType, blockState: BlockState?, tileEntity: TileEntity?)
        : this(world.getChunkByBlockCoord(globalPos.x, globalPos.z), globalToLocalPos(globalPos), blockType, blockState, tileEntity)
}
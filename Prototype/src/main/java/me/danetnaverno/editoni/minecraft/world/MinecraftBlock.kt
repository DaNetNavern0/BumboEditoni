package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.block.BlockType
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.BlockState
import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.common.world.TileEntity
import org.joml.Vector3i

class MinecraftBlock(chunk: Chunk, localPos: Vector3i, blockType: BlockType, blockState: BlockState?, tileEntity: TileEntity?)
    : Block(chunk, localPos, blockType, blockState, tileEntity)

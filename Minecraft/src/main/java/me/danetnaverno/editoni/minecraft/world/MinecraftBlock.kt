package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.BlockState
import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.common.world.TileEntity
import me.danetnaverno.editoni.util.location.BlockLocation

class MinecraftBlock(chunk: Chunk, location: BlockLocation, blockType: BlockType, blockState: BlockState?, tileEntity: TileEntity?)
    : Block(chunk, location, blockType, blockState, tileEntity)
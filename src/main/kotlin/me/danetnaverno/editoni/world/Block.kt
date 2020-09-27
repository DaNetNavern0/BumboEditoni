package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.blockstate.BlockState
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.location.BlockLocation

data class Block(@JvmField val chunk: Chunk, @JvmField val location: BlockLocation, @JvmField val type: BlockType, @JvmField val state: BlockState?, @JvmField val tileEntity: TileEntity?)
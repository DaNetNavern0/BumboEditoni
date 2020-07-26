package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.util.location.BlockLocation

class Block(@JvmField val chunk: Chunk, @JvmField val location: BlockLocation, @JvmField val type: BlockType, @JvmField val state: BlockState?, @JvmField val tileEntity: TileEntity?)
{
}
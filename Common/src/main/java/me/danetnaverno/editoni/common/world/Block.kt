package me.danetnaverno.editoni.common.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.util.location.BlockLocation

abstract class Block(open val chunk: Chunk, open val location: BlockLocation,
                     open val type: BlockType, open val state: BlockState?, open val tileEntity: TileEntity?)
package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.block.BlockDictionary
import me.danetnaverno.editoni.common.world.Block
import net.querz.nbt.CompoundTag

class MinecraftBlock(chunk: MinecraftChunk, x: Int, y: Int, z: Int, tag: CompoundTag) : Block(chunk, x, y, z)
{
    init
    {
        type = BlockDictionary.getBlockType(tag.getString("Name"))
    }
}
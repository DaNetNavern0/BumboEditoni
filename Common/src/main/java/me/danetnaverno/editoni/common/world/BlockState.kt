package me.danetnaverno.editoni.common.world

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blocktype.BlockDictionary
import me.danetnaverno.editoni.common.blocktype.BlockType
import net.querz.nbt.CompoundTag

abstract class BlockState(val tag: CompoundTag)
{
    override fun toString(): String
    {
        return tag.toTagString()
    }

    abstract fun getType(): BlockType
}

class UnknownBlockState(properties: CompoundTag) : BlockState(properties)
{
    override fun getType(): BlockType
    {
        return BlockDictionary.getBlockType(ResourceLocation(tag.getString("Name")))
        //todo mc-bound
    }
}
package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blocktype.BlockDictionary
import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.common.world.BlockState
import net.querz.nbt.CompoundTag

open class MinecraftBlockState(tag: CompoundTag) : BlockState(tag)
{
    override fun getType(): BlockType
    {
        return BlockDictionary.getBlockType(ResourceLocation(tag.getString("Name")))
    }
}
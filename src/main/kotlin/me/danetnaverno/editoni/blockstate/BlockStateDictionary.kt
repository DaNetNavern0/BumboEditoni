package me.danetnaverno.editoni.blockstate

import me.danetnaverno.editoni.blocktype.BlockType
import net.querz.nbt.tag.CompoundTag

object BlockStateDictionary
{
    private val stateTypes = hashMapOf<BlockType, Class<out BlockState>>()

    fun register(blockType: BlockType, stateType: Class<out BlockState>)
    {
        stateTypes[blockType] = stateType
    }

    fun createBlockState(blockType: BlockType, parameters: CompoundTag?): BlockState?
    {
        //todo this could definitely use some optimization, because we essentially call a constructor via reflection
        //  for every Block State in every chunk we load
        //  well, it WOULD, if we'd actually register any block state classes
        val typeClass = stateTypes[blockType] ?: return if (parameters != null) UnknownBlockState(parameters) else null
        val constr = typeClass.getConstructor(CompoundTag::class.java)
        return constr.newInstance(parameters) as BlockState
    }
}
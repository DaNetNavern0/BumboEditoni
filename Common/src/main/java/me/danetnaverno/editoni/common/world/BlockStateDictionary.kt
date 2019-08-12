package me.danetnaverno.editoni.common.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import net.querz.nbt.CompoundTag

object BlockStateDictionary
{
    private val stateTypes = mutableMapOf<BlockType, Class<out BlockState>>()

    @JvmStatic
    fun register(blockType: BlockType, stateType: Class<out BlockState>)
    {
        stateTypes[blockType] = stateType
    }

    @JvmStatic
    fun createBlockState(blockType: BlockType, parameters: CompoundTag?): BlockState?
    {
        val typeClass = stateTypes[blockType] ?: return if (parameters != null) UnknownBlockState(parameters) else null
        val constr = typeClass.getConstructor(CompoundTag::class.java)
        return constr.newInstance(parameters) as BlockState
    }
}
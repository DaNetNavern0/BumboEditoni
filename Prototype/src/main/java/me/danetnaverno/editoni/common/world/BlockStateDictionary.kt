package me.danetnaverno.editoni.common.world

import me.danetnaverno.editoni.common.block.BlockType
import me.danetnaverno.editoni.minecraft.world.UnknownMinecraftBlockState
import net.querz.nbt.CompoundTag
import org.apache.logging.log4j.LogManager

object BlockStateDictionary
{
    private val logger = LogManager.getLogger("BlockDictionary")
    private val stateTypes = mutableMapOf<BlockType, Class<out BlockState>>()

    @JvmStatic
    fun register(blockType: BlockType, stateType: Class<out BlockState>)
    {
        stateTypes[blockType] = stateType
    }

    @JvmStatic
    fun createBlockState(blockType: BlockType, parameters: CompoundTag?): BlockState?
    {
        val typeClass = stateTypes[blockType] ?: return if (parameters!=null) UnknownMinecraftBlockState(parameters) else null
        val constr = typeClass.getConstructor(CompoundTag::class.java)
        return constr.newInstance(parameters) as BlockState
    }
}
package me.danetnaverno.editoni.io

import net.querz.nbt.tag.CompoundTag

class MCAExtraInfo114(data: CompoundTag) : MCAExtraInfo(data)
{
    init
    {
        val level = data.getCompoundTag("Level")
        level.putByte("isLightOn", 0.toByte()) //Invalidating light - Minecraft will recalculate it again
                                               //todo Neighboring chunks need light to be invalidated as well
        //level.remove("Sections")
        //level.remove("Heightmaps")
        //level.remove("Entities")
    }
}
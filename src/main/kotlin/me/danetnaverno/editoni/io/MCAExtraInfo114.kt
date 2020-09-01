package me.danetnaverno.editoni.io

import net.querz.nbt.tag.CompoundTag

class MCAExtraInfo114(data: CompoundTag) : MCAExtraInfo(data)
{
    init
    {
        val level = data.getCompoundTag("Level")
        //level.putByte("isLightOn", 0.toByte())
        //level.remove("Sections")
        //level.remove("Heightmaps")
        //level.remove("Entities")
    }
}
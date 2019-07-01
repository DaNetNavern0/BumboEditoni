package me.danetnaverno.editoni.minecraft.world.io;

import net.querz.nbt.CompoundTag;

public class MCAExtraInfo114 extends MCAExtraInfo
{
    public MCAExtraInfo114(CompoundTag data)
    {
        super(data);
        CompoundTag level = data.getCompoundTag("Level");
        level.putByte("isLightOn", (byte) 0);
        level.remove("Sections");
        level.remove("Heightmaps");
        level.remove("Entities");
    }
}
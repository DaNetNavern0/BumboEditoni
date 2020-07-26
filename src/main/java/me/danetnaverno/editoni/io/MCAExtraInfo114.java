package me.danetnaverno.editoni.io;


import net.querz.nbt.tag.CompoundTag;

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
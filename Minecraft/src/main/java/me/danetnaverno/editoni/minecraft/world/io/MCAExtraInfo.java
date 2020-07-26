package me.danetnaverno.editoni.world.world.io;

import net.querz.nbt.CompoundTag;

/**
 * This class is here to keep the unhandled NBT data from chunks.
 * <p>
 * May or may not be gone in a future in case if all chunk data will be handled.
 */
public class MCAExtraInfo
{
    protected CompoundTag data;

    public MCAExtraInfo(CompoundTag data)
    {
        this.data = data;
    }

    public CompoundTag getData()
    {
        return data.clone();
    }
}

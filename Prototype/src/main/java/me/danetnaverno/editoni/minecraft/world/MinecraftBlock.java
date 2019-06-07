package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.world.Block;
import net.querz.nbt.CompoundTag;

public class MinecraftBlock extends Block
{
    public MinecraftBlock(CompoundTag tag, int x, int y, int z)
    {
        super();
        try
        {
            id = tag.getString("Name");
            this.x = x;
            this.y = y;
            this.z = z;
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
    }
}
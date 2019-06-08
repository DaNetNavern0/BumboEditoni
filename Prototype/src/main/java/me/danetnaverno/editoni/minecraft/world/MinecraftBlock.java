package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.world.Block;
import net.querz.nbt.CompoundTag;

public class MinecraftBlock extends Block
{
    public MinecraftBlock(MinecraftChunk chunk, int x, int y, int z, CompoundTag tag)
    {
        super(chunk, x, y, z);
        try
        {
            setId(tag.getString("Name"));
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
    }
}
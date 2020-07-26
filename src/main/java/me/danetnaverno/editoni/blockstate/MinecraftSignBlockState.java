package me.danetnaverno.editoni.blockstate;

import me.danetnaverno.editoni.world.BlockState;
import net.querz.nbt.tag.CompoundTag;

public class MinecraftSignBlockState extends BlockState
{
    public final String facing;
    public final boolean waterlogged;

    public MinecraftSignBlockState(CompoundTag properties)
    {
        super(properties);
        facing = properties.getString("facing");
        waterlogged = properties.getBoolean("waterlogged");
    }
}

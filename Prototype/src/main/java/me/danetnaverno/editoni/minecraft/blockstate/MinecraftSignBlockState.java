package me.danetnaverno.editoni.minecraft.blockstate;

import me.danetnaverno.editoni.minecraft.world.MinecraftBlockState;
import net.querz.nbt.CompoundTag;

public class MinecraftSignBlockState extends MinecraftBlockState
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

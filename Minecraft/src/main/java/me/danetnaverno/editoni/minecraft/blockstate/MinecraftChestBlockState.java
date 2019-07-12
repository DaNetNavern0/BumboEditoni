package me.danetnaverno.editoni.minecraft.blockstate;

import me.danetnaverno.editoni.minecraft.world.MinecraftBlockState;
import net.querz.nbt.CompoundTag;

public class MinecraftChestBlockState extends MinecraftBlockState
{
    public final String facing;
    public final String type;
    public final boolean waterlogged;

    public MinecraftChestBlockState(CompoundTag properties)
    {
        super(properties);
        facing = properties.getString("facing");
        waterlogged = properties.getBoolean("waterlogged");
        type = properties.getString("type");
    }
}

package me.danetnaverno.editoni.blockstate;

import me.danetnaverno.editoni.world.BlockState;
import net.querz.nbt.tag.CompoundTag;

public class MinecraftChestBlockState extends BlockState
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

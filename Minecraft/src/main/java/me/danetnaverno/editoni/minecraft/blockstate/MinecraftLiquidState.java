package me.danetnaverno.editoni.world.blockstate;

import me.danetnaverno.editoni.world.world.MinecraftBlockState;
import net.querz.nbt.CompoundTag;

public class MinecraftLiquidState extends MinecraftBlockState
{
    public final int level;

    public MinecraftLiquidState(CompoundTag properties)
    {
        super(properties);
        level = Integer.parseInt(properties.getString("level"));
    }
}

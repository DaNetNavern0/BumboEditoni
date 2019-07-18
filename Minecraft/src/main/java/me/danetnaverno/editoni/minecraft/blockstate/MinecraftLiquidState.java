package me.danetnaverno.editoni.minecraft.blockstate;

import me.danetnaverno.editoni.minecraft.world.MinecraftBlockState;
import net.querz.nbt.CompoundTag;

public class MinecraftLiquidState extends MinecraftBlockState
{
    public final int level;

    public MinecraftLiquidState(CompoundTag properties)
    {
        super(properties);
        level = Integer.parseInt(properties.getCompoundTag("Properties").getString("level"));
    }
}

package me.danetnaverno.editoni.blockstate;

import me.danetnaverno.editoni.world.BlockState;
import net.querz.nbt.tag.CompoundTag;

public class MinecraftLiquidState extends BlockState
{
    public final int level;

    public MinecraftLiquidState(CompoundTag properties)
    {
        super(properties);
        level = Integer.parseInt(properties.getString("level"));
    }
}

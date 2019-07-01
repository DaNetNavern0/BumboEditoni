package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.world.Entity;
import net.querz.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class MinecraftEntity extends Entity
{
    public MinecraftEntity(@NotNull CompoundTag tag)
    {
        super(tag);
    }
}

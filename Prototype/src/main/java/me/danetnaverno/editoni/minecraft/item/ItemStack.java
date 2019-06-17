package me.danetnaverno.editoni.minecraft.item;

import me.danetnaverno.editoni.common.ResourceLocation;
import net.querz.nbt.CompoundTag;

public class ItemStack
{
    public final ResourceLocation id;
    public final int count;
    public final CompoundTag nbt;

    public ItemStack(ResourceLocation id)
    {
        this(id, 1, null);
    }

    public ItemStack(ResourceLocation id, int count)
    {
        this(id, count, null);
    }

    public ItemStack(ResourceLocation id, CompoundTag nbt)
    {
        this(id, 1, nbt);
    }

    public ItemStack(ResourceLocation id, int count, CompoundTag nbt)
    {
        this.id = id;
        this.count = count;
        this.nbt = nbt;
    }
}
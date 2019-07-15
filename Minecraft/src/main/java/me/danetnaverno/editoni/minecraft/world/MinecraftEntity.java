package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.entitytype.EntityType;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.common.world.Entity;
import me.danetnaverno.editoni.util.location.EntityLocation;
import net.querz.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class MinecraftEntity extends Entity
{
    public MinecraftEntity(@NotNull Chunk chunk, @NotNull EntityLocation location, @NotNull EntityType type, @NotNull CompoundTag tag)
    {
        super(chunk, location, type, tag);
    }
}

package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.entitytype.EntityType;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.common.world.Entity;
import net.querz.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class MinecraftEntity extends Entity
{
    public MinecraftEntity(@NotNull Chunk chunk, @NotNull Vector3d globalPos, @NotNull EntityType type, @NotNull CompoundTag tag)
    {
        super(chunk, globalPos, type, tag);
    }
}

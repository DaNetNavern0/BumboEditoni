package me.danetnaverno.editoni.minecraft.world.io;

import me.danetnaverno.editoni.common.world.io.IWorldIOProvider;
import me.danetnaverno.editoni.minecraft.util.location.RegionLocation;
import me.danetnaverno.editoni.minecraft.world.MinecraftWorld;
import org.jetbrains.annotations.NotNull;

public interface IMinecraftWorldIOProvider extends IWorldIOProvider
{
    void loadRegion(@NotNull MinecraftWorld world, @NotNull RegionLocation location);
}

package me.danetnaverno.editoni.common.world.io;

import me.danetnaverno.editoni.common.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public interface WorldIOProvider
{
    boolean isAppropriateToRead(@NotNull Path path);

    boolean isAppropriateToWrite(@NotNull World world);

    World readWorld(@NotNull Path path) throws IOException;

    void writeWorld(@NotNull World world, @NotNull Path path) throws IOException;
}

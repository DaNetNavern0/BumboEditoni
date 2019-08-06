package me.danetnaverno.editoni.common.world.io;

import me.danetnaverno.editoni.common.world.World;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public interface IWorldIOProvider
{
    boolean isAppropriateToRead(@NotNull Path path);

    @NotNull
    Collection<World> readWorlds(@NotNull Path path) throws IOException;

    void writeWorld(@NotNull World world, @NotNull Path path) throws IOException;

    void loadChunk(@NotNull World world, @NotNull ChunkLocation location) throws IOException;
}

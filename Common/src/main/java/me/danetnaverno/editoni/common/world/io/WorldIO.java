package me.danetnaverno.editoni.common.world.io;

import me.danetnaverno.editoni.common.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WorldIO
{
    private static List<WorldIOProvider> providers = new ArrayList<>();

    public static World readWorld(@NotNull Path path) throws IOException
    {
        for (WorldIOProvider provider : providers)
            if (provider.isAppropriateToRead(path))
                return provider.readWorld(path);
        throw new IllegalArgumentException("World Provider not found for folder or file '"+path+"'");
    }

    public static void writeWorld(@NotNull World world, @NotNull Path path) throws IOException
    {
        for (WorldIOProvider provider : providers)
            if (provider.isAppropriateToWrite(world))
            {
                provider.writeWorld(world, path);
                return;
            }
        throw new IllegalArgumentException("World Provider not found for world '"+world+"' to save into '"+path+"'");
    }

    public static void registerProvider(WorldIOProvider provider)
    {
        providers.add(provider);
    }
}

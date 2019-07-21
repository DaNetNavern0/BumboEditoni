package me.danetnaverno.editoni.common.world.io;

import me.danetnaverno.editoni.common.world.World;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WorldIO
{
    private static List<IWorldIOProvider> providers = new ArrayList<>();

    public static World readWorld(@NotNull Path path) throws IOException
    {
        for (IWorldIOProvider provider : providers)
            if (provider.isAppropriateToRead(path))
                return provider.readWorld(path);
        throw new IllegalArgumentException("World Provider not found for folder or file '"+path+"'");
    }

    public static void writeWorld(@NotNull World world, @NotNull Path path) throws IOException
    {
        world.worldIOProvider.writeWorld(world, path);
    }

    public static void registerProvider(IWorldIOProvider provider)
    {
        providers.add(provider);
    }
}

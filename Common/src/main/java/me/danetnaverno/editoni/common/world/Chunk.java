package me.danetnaverno.editoni.common.world;

import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import me.danetnaverno.editoni.util.location.EntityLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Chunk
{
    public final World world;
    public final ChunkLocation location;
    public final int renderX;
    public final int renderZ;

    public Chunk(World world, ChunkLocation location, int renderX, int renderZ)
    {
        this.world = world;
        this.location = location;
        this.renderX = renderX;
        this.renderZ = renderZ;
    }

    public abstract @NotNull Iterable<Block> getBlocks();

    public abstract @Nullable Block getBlockAt(@NotNull BlockLocation pos);

    public Block getBlockAt(int x, int y, int z)
    {
        return getBlockAt(new BlockLocation(this, x, y, z));
    }

    public abstract void setBlock(@NotNull Block block);


    public abstract Collection<Entity> getEntities();

    public List<Entity> getEntitiesAt(EntityLocation location, float radius)
    {
        return getEntities().stream().filter( it-> it.getGlobalPos().distance(location) < radius).collect(Collectors.toList());
    }
}

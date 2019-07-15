package me.danetnaverno.editoni.common.world;

import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import me.danetnaverno.editoni.util.location.EntityLocation;

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

    public abstract Collection<Block> getBlocks();

    public abstract Block getBlockAt(BlockLocation pos);

    public Block getBlockAt(int x, int y, int z)
    {
        return getBlockAt(new BlockLocation(this, x, y, z));
    }

    public abstract void setBlock(Block block);


    public abstract Collection<Entity> getEntities();

    public List<Entity> getEntitiesAt(EntityLocation location, float radius)
    {
        return getEntities().stream().filter( it-> it.getGlobalPos().distance(location) < radius).collect(Collectors.toList());
    }

    public abstract boolean isLoaded();
}

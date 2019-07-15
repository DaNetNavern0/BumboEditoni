package me.danetnaverno.editoni.common.world;

import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import me.danetnaverno.editoni.util.location.EntityLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class World
{
    public WorldRenderer worldRenderer;

    public abstract List<Entity> getEntitiesAt(EntityLocation location, float radius);

    public abstract Block getBlockAt(BlockLocation pos);

    public Block getBlockAt(int x, int y, int z)
    {
        return getBlockAt(new BlockLocation(x, y, z));
    }

    public abstract @Nullable Chunk getChunkIfLoaded(ChunkLocation location);

    public abstract Chunk getChunk(ChunkLocation location);

    public abstract Chunk getChunk(BlockLocation location);

    public abstract void setBlock(Block block);

    public abstract BlockType getAirType();
}

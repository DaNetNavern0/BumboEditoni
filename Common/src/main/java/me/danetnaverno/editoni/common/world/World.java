package me.danetnaverno.editoni.common.world;

import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import me.danetnaverno.editoni.util.location.EntityLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class World
{
    public WorldRenderer worldRenderer;

    public abstract @Nullable Chunk getChunkIfLoaded(@NotNull ChunkLocation location);

    public abstract @Nullable Chunk getChunk(@NotNull ChunkLocation location);

    public abstract @Nullable Chunk getChunk(@NotNull BlockLocation location);

    public abstract void loadChunkAt(@NotNull ChunkLocation chunkLocation);

    public abstract List<Entity> getEntitiesAt(EntityLocation location, float radius);

    public abstract Block getBlockAt(BlockLocation pos);

    public Block getBlockAt(int x, int y, int z)
    {
        return getBlockAt(new BlockLocation(x, y, z));
    }

    public abstract void setBlock(@NotNull Block block);
}

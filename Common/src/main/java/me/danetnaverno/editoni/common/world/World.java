package me.danetnaverno.editoni.common.world;

import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.common.world.io.IWorldIOProvider;
import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import me.danetnaverno.editoni.util.location.EntityLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

public abstract class World
{
    public WorldRenderer worldRenderer;
    public IWorldIOProvider worldIOProvider;
    protected Path path;

    @Nullable
    public abstract String getName();

    @Nullable
    public abstract Chunk getChunkIfLoaded(@NotNull ChunkLocation location);

    @Nullable
    public abstract Chunk getChunk(@NotNull ChunkLocation location);

    public abstract void loadChunkAt(@NotNull ChunkLocation chunkLocation);

    public abstract List<Entity> getEntitiesAt(@NotNull EntityLocation location, float radius);

    @Nullable
    public abstract Block getBlockAt(@NotNull BlockLocation location);

    @Nullable
    public abstract BlockType getBlockTypeAt(@NotNull BlockLocation location);

    @Nullable
    public abstract BlockState getBlockStateAt(@NotNull BlockLocation location);

    @Nullable
    public abstract TileEntity getTileEntityAt(@NotNull BlockLocation location);

    @Nullable
    public abstract BlockType getLoadedBlockTypeAt(@NotNull BlockLocation location);

    @Nullable
    public abstract BlockState getLoadedBlockStateAt(@NotNull BlockLocation location);

    @Nullable
    public abstract TileEntity getLoadedTileEntityAt(@NotNull BlockLocation location);

    public abstract void setBlock(@NotNull Block block);

    @NotNull
    public abstract BlockType getAirType();
}

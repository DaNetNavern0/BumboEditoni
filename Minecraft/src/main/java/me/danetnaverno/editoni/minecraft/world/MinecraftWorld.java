package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.common.world.*;
import me.danetnaverno.editoni.common.world.io.IWorldIOProvider;
import me.danetnaverno.editoni.minecraft.MinecraftDictionaryFiller;
import me.danetnaverno.editoni.minecraft.util.location.LocationUtilsKt;
import me.danetnaverno.editoni.minecraft.util.location.RegionLocation;
import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import me.danetnaverno.editoni.util.location.EntityLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class MinecraftWorld extends World
{
    public final String version;
    private final Map<RegionLocation, MinecraftRegion> regions = new HashMap<>();

    public MinecraftWorld(String version, IWorldIOProvider worldIOProvider, Path path)
    {
        this.version = version;
        this.worldRenderer = new MinecraftWorldRenderer(this);
        this.worldIOProvider = worldIOProvider;
        this.path = path;
    }

    @Nullable
    @Override
    public String getName()
    {
        return path.getFileName().toString() + " (" + version + ")";
    }

    @Override
    public Chunk getChunkIfLoaded(ChunkLocation location)
    {
        MinecraftRegion region = getRegion(LocationUtilsKt.toRegionLocation(location));
        if (region == null)
            return null;
        return region.getChunkIfLoaded(location);
    }

    @Override
    public Chunk getChunk(ChunkLocation location)
    {
        MinecraftRegion region = getRegion(LocationUtilsKt.toRegionLocation(location));
        if (region == null)
            return null;
        return region.getChunk(location);
    }

    @Override
    public void loadChunkAt(@NotNull ChunkLocation chunkLocation)
    {
        getRegion(LocationUtilsKt.toRegionLocation(chunkLocation)).loadChunkAt(chunkLocation);
    }

    public Chunk createEmptyChunk(ChunkLocation location)
    {
        return getRegion(LocationUtilsKt.toRegionLocation(location)).createEmptyChunk(location);
    }

    @Override
    public List<Entity> getEntitiesAt(EntityLocation location, float radius)
    {
        Chunk chunk = getChunk(location.toChunkLocation()); //todo radius can touch multiple chunks
        if (chunk == null)
            return null;
        return chunk.getEntitiesAt(location, radius).stream()
                .sorted(Comparator.comparingDouble(it -> it.getLocation().distanceSquared(location)))
                .collect(Collectors.toList());
    }

    @Override
    public Block getBlockAt(@NotNull BlockLocation location)
    {
        Chunk chunk = getChunk(location.toChunkLocation());
        if (chunk==null)
            return null;
        return chunk.getBlockAt(location);
    }

    @Override
    public BlockType getBlockTypeAt(BlockLocation location)
    {
        Chunk chunk = getChunk(location.toChunkLocation());
        if (chunk == null)
            return null;
        return chunk.getBlockTypeAt(location);
    }

    @Override
    public BlockState getBlockStateAt(BlockLocation location)
    {
        Chunk chunk = getChunk(location.toChunkLocation());
        if (chunk == null)
            return null;
        return chunk.getBlockStateAt(location);
    }

    @Override
    public TileEntity getTileEntityAt(BlockLocation location)
    {
        Chunk chunk = getChunk(location.toChunkLocation());
        if (chunk == null)
            return null;
        return chunk.getTileEntityAt(location);
    }

    @Override
    public BlockType getLoadedBlockTypeAt(@NotNull BlockLocation location)
    {
        Chunk chunk = getChunkIfLoaded(location.toChunkLocation());
        if (chunk == null)
            return null;
        return chunk.getBlockTypeAt(location);
    }

    @Override
    public BlockState getLoadedBlockStateAt(@NotNull BlockLocation location)
    {
        Chunk chunk = getChunkIfLoaded(location.toChunkLocation());
        if (chunk == null)
            return null;
        return chunk.getBlockStateAt(location);
    }

    @Override
    public TileEntity getLoadedTileEntityAt(@NotNull BlockLocation location)
    {
        Chunk chunk = getChunkIfLoaded(location.toChunkLocation());
        if (chunk == null)
            return null;
        return chunk.getTileEntityAt(location);
    }

    @Override
    public void setBlock(Block block)
    {
        Chunk chunk = getChunk(block.getLocation().toChunkLocation());
        if (chunk==null)
        {

        }
        chunk.setBlock(block);
    }

    @Override
    public void deleteBlock(@NotNull BlockLocation location)
    {
        Chunk chunk = getChunk(location.toChunkLocation());
        if (chunk != null)
            setBlock(new Block(chunk, location, MinecraftDictionaryFiller.AIR, null, null));
    }

    public Collection<MinecraftRegion> getRegions()
    {
        return new ArrayList<>(regions.values());
    }

    public MinecraftRegion getRegion(RegionLocation location)
    {
        return regions.get(location);
    }

    public void addRegion(MinecraftRegion region)
    {
        regions.put(region.location, region);
    }
}
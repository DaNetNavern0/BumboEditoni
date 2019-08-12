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

    @Override
    public String toString()
    {
        try
        {
            if (path.getParent().getFileName().toString().contains("DIM1"))
                return path.getParent().getParent().getFileName()+" (End; " + version + ")";
            if (path.getParent().getFileName().toString().contains("DIM-1"))
                return path.getParent().getParent().getFileName()+" (Nether; " + version + ")";
            if (path.getFileName().toString().contains("region"))
                return path.getParent().getFileName()+" (" + version + ")";
        }
        catch (Exception e)
        {
            return path.getFileName().toString() + " (" + version + ")";
        }
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

    @NotNull
    @Override
    public List<Chunk> getLoadedChunks()
    {
        return regions.values().stream().flatMap(it -> it.getLoadedChunks().stream()).collect(Collectors.toList());
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

    @Override
    public void unloadChunks(List<Chunk> chunksToUnload)
    {
        for (Chunk chunk : chunksToUnload)
        {
            getRegion(LocationUtilsKt.toRegionLocation(chunk.location)).unloadChunk(chunk.location);
        }
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
    public Block getLoadedBlockAt(@NotNull BlockLocation location)
    {
        Chunk chunk = getChunkIfLoaded(location.toChunkLocation());
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
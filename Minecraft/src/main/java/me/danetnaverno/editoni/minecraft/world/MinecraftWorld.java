package me.danetnaverno.editoni.world.world;

import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.common.world.*;
import me.danetnaverno.editoni.common.world.io.IWorldIOProvider;
import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import me.danetnaverno.editoni.util.location.EntityLocation;
import me.danetnaverno.editoni.world.MinecraftDictionaryFiller;
import me.danetnaverno.editoni.world.util.location.LocationUtilsKt;
import me.danetnaverno.editoni.world.util.location.RegionLocation;
import org.jetbrains.annotations.NotNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.file.Path;
import java.util.stream.Collectors;

public class MinecraftWorld extends World
{
    public final String version;
    private final Map<RegionLocation, MinecraftRegion> regions = new HashMap<>();
    private Path path;

    public MinecraftWorld(String version, IWorldIOProvider worldIOProvider, Path path)
    {
        this.version = version;
        this.setWorldRenderer(new MinecraftWorldRenderer(this));
        this.setWorldIOProvider(worldIOProvider);
        this.setPath(path);
    }

    //======================
    // REGIONS
    //======================
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

    //======================
    // CHUNKS
    //======================
    @Override
    public List<Chunk> getLoadedChunks()
    {
        return regions.values().stream().flatMap(it -> it.getLoadedChunks().stream()).collect(Collectors.toList());
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

    @Override
    public void unloadChunks(@NotNull List<? extends Chunk> chunksToUnload)
    {
        for (Chunk chunk : chunksToUnload)
            getRegion(LocationUtilsKt.toRegionLocation(chunk.location)).unloadChunk(chunk.location);
    }

    @NotNull
    public MinecraftChunk createChunk(@NotNull ChunkLocation location)
    {
        throw new NotImplementedException();
    }

    //======================
    // BLOCKS
    //======================
    @Override
    public Block getBlockAt(@NotNull BlockLocation location)
    {
        Chunk chunk = getChunk(location.toChunkLocation());
        if (chunk == null)
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
    public Block getLoadedBlockAt(@NotNull BlockLocation location)
    {
        Chunk chunk = getChunkIfLoaded(location.toChunkLocation());
        if (chunk == null)
            return null;
        return chunk.getBlockAt(location);
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
        if (chunk == null)
            chunk = createChunk(block.getLocation().toChunkLocation());
        chunk.setBlock(block);
    }

    @Override
    public void deleteBlock(@NotNull BlockLocation location)
    {
        Chunk chunk = getChunk(location.toChunkLocation());
        if (chunk != null)
            setBlock(new MinecraftBlock(chunk, location, MinecraftDictionaryFiller.AIR, null, null));
    }


    //======================
    // ENTITIES
    //======================
    @Override
    public List<Entity> getEntitiesAt(EntityLocation location, float radius)
    {
        return getLoadedChunks().stream()
                .filter(chunk -> chunk.location.distance(location.toChunkLocation()) <= 1)
                .flatMap(chunk -> chunk.getEntitiesAt(location, radius).stream())
                .sorted(Comparator.comparingDouble(entity -> entity.getLocation().distanceSquared(location)))
                .collect(Collectors.toList());
    }

    //======================
    // UTIL
    //======================
    @NotNull
    @Override
    public Path getPath()
    {
        return path;
    }

    @Override
    protected void setPath(@NotNull Path path)
    {
        this.path = path;
    }

    @Override
    public String toString()
    {
        try
        {
            if (getPath().getParent().getFileName().toString().contains("DIM1"))
                return getPath().getParent().getParent().getFileName() + " (End; " + version + ")";
            if (getPath().getParent().getFileName().toString().contains("DIM-1"))
                return getPath().getParent().getParent().getFileName() + " (Nether; " + version + ")";
            if (getPath().getFileName().toString().contains("region"))
                return getPath().getParent().getFileName() + " (" + version + ")";
        }
        catch (Exception e)
        {
            return getPath().getFileName().toString() + " (" + version + ")";
        }
        return getPath().getFileName().toString() + " (" + version + ")";
    }
}
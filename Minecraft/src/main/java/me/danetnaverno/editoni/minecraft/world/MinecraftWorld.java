package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.common.world.Entity;
import me.danetnaverno.editoni.common.world.World;
import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import me.danetnaverno.editoni.util.location.EntityLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.*;
import java.util.stream.Collectors;

public class MinecraftWorld extends World
{
    public final String version;
    private final Map<Vector2i, MinecraftRegion> regions = new HashMap<>();

    public MinecraftWorld(String version)
    {
        this.version = version;
        this.worldRenderer = new MinecraftWorldRenderer(this);
    }

    @Override
    public Chunk getChunkIfLoaded(ChunkLocation location)
    {
        MinecraftRegion region = getRegion(location.x >> 6, location.z >> 6);
        if (region == null)
            return null;
        return region.getChunkIfLoaded(location);
    }

    @Override
    public Chunk getChunk(ChunkLocation location)
    {
        MinecraftRegion region = getRegion(location.x >> 6, location.z >> 6);
        if (region == null)
            return null;
        return region.getChunk(location);
    }

    @Override
    public Chunk getChunk(BlockLocation location)
    {
        return getChunk(location.toChunkLocation());
    }

    @Override
    public void loadChunkAt(@NotNull ChunkLocation chunkLocation)
    {
        getRegion(chunkLocation.x >> 4, chunkLocation.z >> 4).loadChunkAt(chunkLocation);
    }

    @Override
    public List<Entity> getEntitiesAt(EntityLocation location, float radius)
    {
        Chunk chunk = getChunk(location.toChunkLocation()); //todo radius can touch multiple chunks
        if (chunk == null)
            return null;
        return chunk.getEntitiesAt(location, radius).stream()
                .sorted(Comparator.comparingDouble(it -> it.getGlobalPos().distanceSquared(location)))
                .collect(Collectors.toList());
    }

    @Override
    public Block getBlockAt(BlockLocation location)
    {
        Chunk chunk = getChunk(location);
        if (chunk == null)
            return null;
        return chunk.getBlockAt(location);
    }

    @Override
    public void setBlock(Block block)
    {
        Chunk chunk = getChunk(block.getLocation().toChunkLocation());
        chunk.setBlock(block);
    }

    public Collection<MinecraftRegion> getRegions()
    {
        return new ArrayList<>(regions.values());
    }

    public MinecraftRegion getRegion(int regionX, int regionZ)
    {
        return regions.get(new Vector2i(regionX, regionZ));
    }

    public MinecraftRegion getRegion(BlockLocation location)
    {
        return getRegion(location.globalX >> 10, location.globalZ >> 10);
    }

    public void addRegion(MinecraftRegion region)
    {
        regions.put(new Vector2i(region.getX(), region.getZ()), region);
    }
}
package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.ResourceLocation;
import me.danetnaverno.editoni.common.blocktype.BlockDictionary;
import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.common.world.Entity;
import me.danetnaverno.editoni.common.world.World;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;

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
    public List<Entity> getEntitiesAt(Vector3d pos, float radius)
    {
        Chunk chunk = getChunkByBlockCoord((int) pos.x, (int) pos.z); //todo radius can touch multiple chunks
        if (chunk == null)
            return null;
        List<Entity> ass = chunk.getEntitiesAt(pos, radius).stream()
                .sorted(Comparator.comparingDouble(it -> it.getGlobalPos().distanceSquared(pos)))
                .collect(Collectors.toList());
        return ass;
    }

    @Override
    public Block getBlockAt(Vector3i pos)
    {
        Chunk chunk = getChunkByBlockCoord(pos.x, pos.z);
        if (chunk==null)
            return null;
        return chunk.getBlockAt(pos.x & 15, pos.y, pos.z & 15);
    }

    public Chunk getRegionByBlockCoord(int blockX, int blockZ)
    {
        return getChunkByChunkCoord(blockX >> 10, blockZ >> 10);
    }

    @Override
    public Chunk getChunkByChunkCoord(int chunkX, int chunkZ)
    {
        MinecraftRegion region = getRegion(chunkX >> 6, chunkZ >> 6);
        if (region == null)
            return null;
        return region.getChunkByChunkCoord(chunkX, chunkZ);
    }

    @Override
    public Chunk getChunkByBlockCoord(int blockX, int blockZ)
    {
        return getChunkByChunkCoord(blockX >> 4, blockZ >> 4);
    }

    @Override
    public void setBlock(Block block)
    {
        Chunk chunk = getChunkByBlockCoord(block.getGlobalX(), block.getGlobalZ());
        chunk.setBlock(block);
    }

    @Override
    public BlockType getAirType()
    {
        return BlockDictionary.getBlockType(new ResourceLocation("minecraft", "air"));
    }

    public Collection<MinecraftRegion> getRegions()
    {
        return new ArrayList<>(regions.values());
    }

    public MinecraftRegion getRegion(int regionX, int regionZ)
    {
        return regions.get(new Vector2i(regionX, regionZ));
    }

    public void addRegion(MinecraftRegion region)
    {
        regions.put(new Vector2i(region.x, region.z), region);
    }
}
package me.danetnaverno.editoni.minecraft.world;

import kotlin.Pair;
import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.common.world.World;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MinecraftWorld extends World
{
    private final Map<Pair<Integer, Integer>, MinecraftRegion> regions = new HashMap<>();

    @Override
    public Block getBlockAt(Vector3i pos)
    {
        Chunk chunk = getChunkByBlockCoord(pos.x, pos.z);
        if (chunk==null)
            return null;
        return chunk.getBlockAt(pos.x & 15, pos.y, pos.z & 15);
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

    public Collection<MinecraftRegion> getRegions()
    {
        return new ArrayList<>(regions.values());
    }

    public MinecraftRegion getRegion(int regionX, int regionZ)
    {
        return regions.get(new Pair<>(regionX, regionZ));
    }

    public void addRegion(MinecraftRegion region)
    {
        regions.put(new Pair<>(region.x, region.z), region);
    }
}
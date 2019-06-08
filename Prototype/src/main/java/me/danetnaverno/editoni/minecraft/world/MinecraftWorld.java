package me.danetnaverno.editoni.minecraft.world;

import kotlin.Pair;
import kotlin.Triple;
import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.common.world.World;

import java.util.HashMap;
import java.util.Map;

public class MinecraftWorld extends World
{
    public Map<Pair<Integer, Integer>, MinecraftRegion> regions = new HashMap<>();

    @Override
    public Block getBlockAt(int x, int y, int z)
    {
        Chunk chunk = getChunkByBlockCoord(x, z);
        if (chunk==null)
            return null;
        return chunk.getBlockAt(x & 15, y, z & 15);
    }

    @Override
    public Chunk getChunkByChunkCoord(int chunkX, int chunkZ)
    {
        return getRegion(chunkX >> 6, chunkZ >> 6).getChunkByChunkCoord(chunkX, chunkZ);
    }

    @Override
    public Chunk getChunkByBlockCoord(int blockX, int blockZ)
    {
        return getChunkByChunkCoord(blockX >> 4, blockZ >> 4);
    }

    public MinecraftRegion getRegion(int regionX, int regionZ)
    {
        return regions.get(new Pair<>(regionX, regionZ));
    }


    public void addRegion(MinecraftRegion region)
    {
        regions.put(new Pair<>(region.x, region.z), region);
    }

    public Triple<MinecraftRegion, Integer, Integer> convertChunkCoord(int chunkX, int chunkZ)
    {
        int regionX = chunkX >> 8;
        int regionZ = chunkZ >> 8;
        return null;
    }
}
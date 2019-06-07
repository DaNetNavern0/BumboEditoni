package me.danetnaverno.editoni.minecraft.world;

import kotlin.Triple;
import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.common.world.World;

import java.util.ArrayList;
import java.util.List;

public class MinecraftWorld extends World
{
    public List<MinecraftRegion> regions = new ArrayList<>();

    @Override
    public Block getBlockAt(int x, int y, int z)
    {
        return null;
    }

    @Override
    public Chunk getChunkByChunkCoord(int chunkX, int chunkZ)
    {
        return null;
    }

    @Override
    public Chunk getChunkByBlockCoord(int blockX, int blockZ)
    {
        return getChunkByChunkCoord(blockX >> 4, blockZ >> 4);
    }

    public void addRegion(MinecraftRegion region)
    {
        regions.add(region);
    }

    public Triple<MinecraftRegion, Integer, Integer> convertChunkCoord(int chunkX, int chunkZ)
    {
        int regionX = chunkX >> 8;
        int regionZ = chunkZ >> 8;
        return null;
    }
}

package me.danetnaverno.editoni.minecraft.world;

import javafx.util.Pair;
import me.danetnaverno.editoni.common.world.Chunk;
import net.querz.nbt.mca.MCAFile;

import java.util.HashMap;
import java.util.Map;

public class MinecraftRegion
{
    public int x,z;

    public Map<Pair<Integer,Integer>,Chunk> chunks = new HashMap<>();

    public MinecraftRegion(MCAFile mcaFile, int regionX, int regionZ)
    {
        x = regionX;
        z = regionZ;
        for (int x = 0; x < 32; x++)
            for (int z = 0; z < 32; z++)
            {
                net.querz.nbt.mca.Chunk mcaChunk = mcaFile.getChunk(x, z);
                if (mcaChunk != null)
                {
                    Chunk chunk = new MinecraftChunk(mcaChunk, x, z);
                    chunks.put(new Pair<>(chunk.xPos, chunk.zPos), chunk);
                }
            }
    }

    public Chunk getChunkByChunkCoord(int chunkX, int chunkZ)
    {
        return chunks.get(new Pair<>(chunkX,chunkZ));
    }

    public Chunk getChunkByBlockCoord(int blockX, int blockZ)
    {
        return getChunkByChunkCoord(blockX >> 4, blockZ >> 4);
    }

}
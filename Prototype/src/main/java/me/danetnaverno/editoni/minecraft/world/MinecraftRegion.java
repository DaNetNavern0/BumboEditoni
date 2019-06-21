package me.danetnaverno.editoni.minecraft.world;

import javafx.util.Pair;
import me.danetnaverno.editoni.common.world.Chunk;
import net.querz.nbt.mca.MCAFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MinecraftRegion
{
    public final int x;
    public final int z;

    private Map<Pair<Integer,Integer>,Chunk> chunks = new HashMap<>();

    public MinecraftRegion(MCAFile mcaFile, int regionX, int regionZ)
    {
        x = regionX;
        z = regionZ;
        for (int renderX = 0; renderX < 32; renderX++)
            for (int renderZ = 0; renderZ < 32; renderZ++)
            {
                net.querz.nbt.mca.Chunk mcaChunk = mcaFile.getChunk(renderX, renderZ);
                if (mcaChunk != null)
                {
                    Chunk chunk = new MinecraftChunk(mcaChunk, renderX, renderZ);
                    chunks.put(new Pair<>(chunk.getPosX(), chunk.getPosZ()), chunk);
                }
            }
    }

    public Collection<Chunk> getChunks()
    {
        return new ArrayList<>(chunks.values());
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
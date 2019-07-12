package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.world.Chunk;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MinecraftRegion
{
    public final int x;
    public final int z;

    protected final Map<Vector2i, MinecraftChunk> chunks = new HashMap<>();

    public MinecraftRegion(int x, int z)
    {
        this.x = x;
        this.z = z;
    }

    public Collection<MinecraftChunk> getChunks()
    {
        return new ArrayList<>(chunks.values());
    }

    public void setChunk(MinecraftChunk chunk)
    {
        chunks.put(new Vector2i(chunk.getPosX(), chunk.getPosZ()), chunk);
    }

    public Chunk getChunkByChunkCoord(int chunkX, int chunkZ)
    {
        return chunks.get(new Vector2i(chunkX,chunkZ));
    }

    public Chunk getChunkByBlockCoord(int blockX, int blockZ)
    {
        return getChunkByChunkCoord(blockX >> 4, blockZ >> 4);
    }
}
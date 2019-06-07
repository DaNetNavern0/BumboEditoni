package me.danetnaverno.editoni.common.world;

public abstract class World
{
    public abstract Block getBlockAt(int x, int y, int z);
    public abstract Chunk getChunkByChunkCoord(int chunkX, int chunkZ);
    public abstract Chunk getChunkByBlockCoord(int blockX, int blockZ);
}

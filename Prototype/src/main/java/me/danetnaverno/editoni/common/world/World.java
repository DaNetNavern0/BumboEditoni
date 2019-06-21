package me.danetnaverno.editoni.common.world;

import org.joml.Vector3i;

public abstract class World
{
    public abstract Block getBlockAt(Vector3i pos);

    public Block getBlockAt(int x, int y, int z)
    {
        return getBlockAt(new Vector3i(x, y, z));
    }

    public abstract Chunk getChunkByChunkCoord(int chunkX, int chunkZ);

    public abstract Chunk getChunkByBlockCoord(int blockX, int blockZ);

    public abstract void setBlock(Block block);
}

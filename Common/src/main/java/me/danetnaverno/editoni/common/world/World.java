package me.danetnaverno.editoni.common.world;

import me.danetnaverno.editoni.common.blocktype.BlockType;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.List;

public abstract class World
{
    public WorldRenderer worldRenderer;

    public abstract List<Entity> getEntitiesAt(Vector3d pos, float radius);

    public abstract Block getBlockAt(Vector3i pos);

    public Block getBlockAt(int x, int y, int z)
    {
        return getBlockAt(new Vector3i(x, y, z));
    }

    public abstract Chunk getChunkByChunkCoord(int chunkX, int chunkZ);

    public abstract Chunk getChunkByBlockCoord(int blockX, int blockZ);

    public abstract void setBlock(Block block);

    public abstract BlockType getAirType();
}

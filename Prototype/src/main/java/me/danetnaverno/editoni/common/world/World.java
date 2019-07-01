package me.danetnaverno.editoni.common.world;

import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.editor.Editor;
import org.joml.Vector3i;

public abstract class World
{
    public WorldRenderer worldRenderer;

    public static World getCurrentWorld()
    {
        return Editor.INSTANCE.getCurrentWorld();
    }

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

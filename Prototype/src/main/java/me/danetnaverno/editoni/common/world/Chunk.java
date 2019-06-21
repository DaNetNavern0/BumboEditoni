package me.danetnaverno.editoni.common.world;

import org.joml.Vector3i;

import java.util.Collection;

public abstract class Chunk
{
    protected int xRender;
    protected int zRender;
    protected int xPos;
    protected int zPos;

    public abstract Collection<Block> getBlocks();

    public abstract Block getBlockAt(Vector3i pos);

    public Block getBlockAt(int x, int y, int z)
    {
        return getBlockAt(new Vector3i(x, y, z));
    }

    public abstract void setBlock(Block block);

    public int getRenderX()
    {
        return xRender;
    }

    public int getRenderZ()
    {
        return zRender;
    }

    public int getPosX()
    {
        return xPos;
    }

    public int getPosZ()
    {
        return zPos;
    }
}

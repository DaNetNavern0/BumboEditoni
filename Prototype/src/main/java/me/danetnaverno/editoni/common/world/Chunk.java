package me.danetnaverno.editoni.common.world;

import java.util.Collection;

public abstract class Chunk
{
    public int xRender, zRender;
    public int xPos, zPos;

    public abstract Collection<Block> getBlocks();

    public abstract Block getBlockAt(int x, int y, int z);
}

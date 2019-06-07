package me.danetnaverno.editoni.common.world;

import java.util.List;

public abstract class Chunk
{
    public int x, z;

    public abstract List<Block> getBlocks();

    public abstract Block getBlockAt(int x, int y, int z);
}

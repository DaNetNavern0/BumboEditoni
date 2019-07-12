package me.danetnaverno.editoni.common.world;

import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Chunk
{
    protected int xRender;
    protected int zRender;
    protected int xPos;
    protected int zPos;
    protected boolean isLoaded = true;

    public Chunk(int xRender, int zRender, int xPos, int zPos)
    {
        this.xRender = xRender;
        this.zRender = zRender;
        this.xPos = xPos;
        this.zPos = zPos;
    }

    public abstract Collection<Block> getBlocks();

    public abstract Block getBlockAt(Vector3i pos);

    public Block getBlockAt(int x, int y, int z)
    {
        return getBlockAt(new Vector3i(x, y, z));
    }

    public abstract void setBlock(Block block);


    public abstract Collection<Entity> getEntities();

    public List<Entity> getEntitiesAt(Vector3d pos, float radius)
    {
        return getEntities().stream().filter( it-> it.getGlobalPos().distance(pos) < radius).collect(Collectors.toList());
    }


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

    public boolean isLoaded()
    {
        return isLoaded;
    }

}

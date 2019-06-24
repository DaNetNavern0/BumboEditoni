package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import org.joml.Vector3i;

import java.util.Collection;
import java.util.Map;

public class MinecraftChunk extends Chunk
{
    private final Map<Vector3i, Block> blocks;
    public final net.querz.nbt.mca.Chunk mcaChunk; //todo protection level

    public MinecraftChunk(net.querz.nbt.mca.Chunk mcaChunk, int xRender, int zRender, int xPos, int zPos, Map<Vector3i, Block> blocks)
    {
        this.mcaChunk = mcaChunk;
        this.xRender = xRender;
        this.zRender = zRender;
        this.xPos = xPos;
        this.zPos = zPos;
        this.blocks = blocks;
    }

    @Override
    public Collection<Block> getBlocks()
    {
        return blocks.values();
    }

    @Override
    public Block getBlockAt(Vector3i pos)
    {
        return blocks.get(pos);
    }

    @Override
    public void setBlock(Block block)
    {
        blocks.put(block.getLocalPos(), block);
    }

}
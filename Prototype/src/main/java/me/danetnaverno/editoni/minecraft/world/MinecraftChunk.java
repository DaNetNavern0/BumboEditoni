package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import me.danetnaverno.editoni.minecraft.world.io.MCAExtraInfo;
import org.joml.Vector3i;

import java.util.Collection;
import java.util.Map;

public class MinecraftChunk extends Chunk
{
    private final Map<Vector3i, Block> blocks;
    public final MCAExtraInfo extras;

    public MinecraftChunk(MCAExtraInfo extras, int xRender, int zRender, int xPos, int zPos, Map<Vector3i, Block> blocks)
    {
        this.extras = extras;
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
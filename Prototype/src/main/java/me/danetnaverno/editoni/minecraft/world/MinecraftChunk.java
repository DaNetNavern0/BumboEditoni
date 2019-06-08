package me.danetnaverno.editoni.minecraft.world;

import kotlin.Triple;
import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import net.querz.nbt.CompoundTag;

import java.lang.reflect.Field;
import java.util.*;

public class MinecraftChunk extends Chunk
{
    public Map<Triple<Integer, Integer, Integer>, Block> blocks = new HashMap<>();

    private static Field dataField;

    static
    {
        try
        {
            dataField = net.querz.nbt.mca.Chunk.class.getDeclaredField("data");
            dataField.setAccessible(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public MinecraftChunk(net.querz.nbt.mca.Chunk mcaChunk, int chunkX, int chunkZ)
    {
        super();
        CompoundTag data = getData(mcaChunk);
        xRender = chunkX;
        zRender = chunkZ;
        xPos = data.getCompoundTag("Level").getInt("xPos");
        zPos = data.getCompoundTag("Level").getInt("zPos");
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 255; y++)
                for (int z = 0; z < 16; z++)
                {
                    try
                    {
                        CompoundTag tag = mcaChunk.getBlockStateAt(x, y, z);
                        if (tag != null)
                            blocks.put(new Triple<>(x, y, z), new MinecraftBlock(this, x, y, z, tag));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
    }

    private CompoundTag getData(net.querz.nbt.mca.Chunk mcaChunk)
    {
        try
        {
            return (CompoundTag) dataField.get(mcaChunk);
        }
        catch (IllegalAccessException ignored)
        {
        }
        return null;
    }

    @Override
    public Collection<Block> getBlocks()
    {
        return blocks.values();
    }

    @Override
    public Block getBlockAt(int x, int y, int z)
    {
        return blocks.get(new Triple<>(x, y, z));
    }
}
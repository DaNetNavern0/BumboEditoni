package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import net.querz.nbt.CompoundTag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MinecraftChunk extends Chunk
{
    public List<Block> blocks = new ArrayList<>();
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
        x = chunkX;
        z = chunkZ;
        //x = data.getInt("xPos");
        //z = data.getInt("zPos");
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 255; y++)
                for (int z = 0; z < 16; z++)
                {
                    try
                    {
                        CompoundTag tag = mcaChunk.getBlockStateAt(x, y, z);
                        if (tag != null)
                            blocks.add(new MinecraftBlock(tag, x, y, z));
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
    public List<Block> getBlocks()
    {
        return blocks;
    }

    @Override
    public Block getBlockAt(int x, int y, int z)
    {
        return null;
    }
}

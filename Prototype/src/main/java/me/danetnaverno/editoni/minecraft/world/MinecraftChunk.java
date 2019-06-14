package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.world.Block;
import me.danetnaverno.editoni.common.world.Chunk;
import net.querz.nbt.CompoundTag;
import org.joml.Vector3i;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MinecraftChunk extends Chunk
{
    public Map<Vector3i, Block> blocks = new HashMap<>();
    public Map<Vector3i, MinecraftTileEntity> tileEntities = new HashMap<>();

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

        for (CompoundTag tileEntity : mcaChunk.getTileEntities())
        {
            int globalX = tileEntity.getInt("x");
            int y = tileEntity.getInt("y");
            int globalZ = tileEntity.getInt("z");
            int x = globalX - (xPos << 4);
            int z = globalZ - (zPos << 4);
            Vector3i pos = new Vector3i(x, y, z);
            tileEntities.put(pos, new MinecraftTileEntity(this, x, y, z, tileEntity));
        }

        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 255; y++)
                for (int z = 0; z < 16; z++)
                {
                    try
                    {
                        CompoundTag tag = mcaChunk.getBlockStateAt(x, y, z);
                        if (tag != null)
                        {
                            Block block = new MinecraftBlock(this, x, y, z, tag, tileEntities.get(new Vector3i(x, y, z)));
                            blocks.put(new Vector3i(x, y, z), block);
                        }
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
        return blocks.get(new Vector3i(x, y, z));
    }
}
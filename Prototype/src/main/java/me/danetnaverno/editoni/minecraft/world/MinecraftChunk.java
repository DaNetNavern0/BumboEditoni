package me.danetnaverno.editoni.minecraft.world;

import me.danetnaverno.editoni.common.ResourceLocation;
import me.danetnaverno.editoni.common.block.BlockDictionary;
import me.danetnaverno.editoni.common.block.BlockType;
import me.danetnaverno.editoni.common.world.*;
import net.querz.nbt.CompoundTag;
import org.joml.Vector3i;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MinecraftChunk extends Chunk
{
    private final Map<Vector3i, Block> blocks = new HashMap<>();

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
        CompoundTag data = getData(mcaChunk);
        xRender = chunkX;
        zRender = chunkZ;
        xPos = data.getCompoundTag("Level").getInt("xPos");
        zPos = data.getCompoundTag("Level").getInt("zPos");

        Map<Vector3i, MinecraftTileEntity> tileEntities = new HashMap<>();
        for (CompoundTag tileEntity : mcaChunk.getTileEntities())
        {
            int globalX = tileEntity.getInt("x");
            int y = tileEntity.getInt("y");
            int globalZ = tileEntity.getInt("z");
            int x = globalX - (getPosX() << 4);
            int z = globalZ - (getPosZ() << 4);
            Vector3i pos = new Vector3i(x, y, z);
            tileEntities.put(pos, new MinecraftTileEntity(tileEntity));
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
                            BlockType blockType = BlockDictionary.getBlockType(new ResourceLocation(tag.getString("Name")));
                            BlockState blockState = BlockStateDictionary.createBlockState(blockType,tag.getCompoundTag("Properties"));
                            TileEntity tileEntity = tileEntities.get(new Vector3i(x, y, z));
                            Block block = new MinecraftBlock(this, new Vector3i(x, y, z), blockType, blockState, tileEntity);
                            blocks.put(new Vector3i(x, y, z), block);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
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
}
package me.danetnaverno.editoni.minecraft.world.io;

import me.danetnaverno.editoni.common.ResourceLocation;
import me.danetnaverno.editoni.common.block.BlockDictionary;
import me.danetnaverno.editoni.common.block.BlockType;
import me.danetnaverno.editoni.common.world.*;
import me.danetnaverno.editoni.minecraft.world.*;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.ListTag;
import net.querz.nbt.NBTUtil;
import net.querz.nbt.mca.Chunk;
import net.querz.nbt.mca.MCAFile;
import net.querz.nbt.mca.MCAUtil;
import net.querz.nbt.mca.Section;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Minecraft113WorldIO
{
    private static Pattern mcaRegex = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca");
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

    public static MinecraftWorld readWorld(File worldFolder) throws IOException
    {
        MinecraftWorld world = new MinecraftWorld();
        File regionFolder = new File(worldFolder, "region");
        for (File file : regionFolder.listFiles())
        {
            Matcher matcher = mcaRegex.matcher(file.getName());
            if (matcher.matches())
            {
                int x = Integer.parseInt(matcher.group(1));
                int z = Integer.parseInt(matcher.group(2));
                world.addRegion(readRegion(MCAUtil.readMCAFile(file), x, z));
            }
        }
        return world;
    }

    public static MinecraftRegion readRegion(MCAFile mcaFile, int x, int z) throws IOException
    {
        MinecraftRegion region = new MinecraftRegion(x, z);

        for (int renderX = 0; renderX < 32; renderX++)
            for (int renderZ = 0; renderZ < 32; renderZ++)
            {
                net.querz.nbt.mca.Chunk mcaChunk = mcaFile.getChunk(renderX, renderZ);
                if (mcaChunk != null)
                {
                    MinecraftChunk chunk = readChunk(mcaChunk, renderX, renderZ);
                    region.setChunk(chunk);
                }
            }
        return region;
    }

    public static MinecraftChunk readChunk(net.querz.nbt.mca.Chunk mcaChunk, int renderX, int renderZ)
    {
        CompoundTag data = getData(mcaChunk);
        int posX = data.getCompoundTag("Level").getInt("xPos");
        int posZ = data.getCompoundTag("Level").getInt("zPos");
        Map<Vector3i, Block> blocks = new HashMap<>();
        MinecraftChunk chunk = new MinecraftChunk(mcaChunk, renderX, renderZ, posX, posZ, blocks);

        Map<Vector3i, MinecraftTileEntity> tileEntities = new HashMap<>();
        for (CompoundTag tileEntity : mcaChunk.getTileEntities())
        {
            int globalX = tileEntity.getInt("x");
            int y = tileEntity.getInt("y");
            int globalZ = tileEntity.getInt("z");
            int x = globalX - (posX << 4);
            int z = globalZ - (posZ << 4);
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
                            BlockState blockState = BlockStateDictionary.createBlockState(blockType, tag.getCompoundTag("Properties"));
                            TileEntity tileEntity = tileEntities.get(new Vector3i(x, y, z));
                            Block block = new MinecraftBlock(chunk, new Vector3i(x, y, z), blockType, blockState, tileEntity);
                            blocks.put(new Vector3i(x, y, z), block);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
        return chunk;
    }

    public static void writeWorld(@NotNull World world, @NotNull File worldFolder) throws IOException
    {
        Files.createDirectories(worldFolder.toPath().resolve("region"));
        MinecraftWorld mcWorld = (MinecraftWorld) world;
        File regionFolder = new File(worldFolder, "region");
        for (MinecraftRegion region : mcWorld.getRegions())
            writeRegion(region, new File(regionFolder, "r." + region.x + "." + region.z + ".mca"));
    }

    public static void writeWorld114(@NotNull World world, @NotNull File worldFolder) throws IOException
    {
        MinecraftWorld mcWorld = (MinecraftWorld) world;
        writeWorld(mcWorld, worldFolder);
        File levelDatFile = new File(worldFolder, "level.dat");
        CompoundTag levelDat = (CompoundTag) NBTUtil.readTag(levelDatFile);
        CompoundTag versionTag = levelDat.getCompoundTag("Data").getCompoundTag("Version");
        //Yes, this method works... At least for vanilla 1.13->1.14...
        versionTag.putString("Name", "1.14.2");
        versionTag.putInt("Id", 1963); //todo move to config
        NBTUtil.writeTag(levelDat, levelDatFile);
    }

    public static void writeRegion(@NotNull MinecraftRegion region, @NotNull File regionFile) throws IOException
    {
        MCAFile result = new MCAFile(region.x, region.z);
        for (MinecraftChunk chunk : region.getChunks())
            result.setChunk(chunk.getRenderX(), chunk.getRenderZ(), writeChunk(chunk));
        MCAUtil.writeMCAFile(result, regionFile);
    }

    public static Chunk writeChunk(@NotNull MinecraftChunk chunk)
    {
        ListTag<CompoundTag> tileEntities = new ListTag<>(CompoundTag.class);
        for (Block block : chunk.getBlocks())
        {
            CompoundTag properties = block.getState() != null ? block.getState().getTag() : null;
            CompoundTag blockState = new CompoundTag();
            blockState.putString("Name", block.getType().toString());
            if (properties != null)
                blockState.put("Properties", properties);
            chunk.mcaChunk.setBlockStateAt(block.getGlobalX(), block.getGlobalY(), block.getGlobalZ(), blockState, false);

            CompoundTag tileEntity = block.getTileEntity() != null ? block.getTileEntity().getTag() : null;
            if (tileEntity != null)
                tileEntities.add(tileEntity);
        }
        chunk.mcaChunk.setTileEntities(tileEntities);

        for (int i = 0; i < 16; i++)
        {
            Section section = chunk.mcaChunk.getSection(i);
            if (section != null)
            {
                section.blockLight = null;
                section.skyLight = null;
            }
        }
        chunk.mcaChunk.updateHandle(chunk.getRenderX(), chunk.getRenderZ());
        chunk.mcaChunk.data.getCompoundTag("Level").putByte("isLightOn", (byte) 0);
        return chunk.mcaChunk;
    }

    private static CompoundTag getData(net.querz.nbt.mca.Chunk mcaChunk)
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

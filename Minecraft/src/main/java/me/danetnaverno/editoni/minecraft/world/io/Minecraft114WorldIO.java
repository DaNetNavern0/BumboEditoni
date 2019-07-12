package me.danetnaverno.editoni.minecraft.world.io;

import me.danetnaverno.editoni.common.ResourceLocation;
import me.danetnaverno.editoni.common.blocktype.BlockDictionary;
import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.common.entitytype.EntityDictionary;
import me.danetnaverno.editoni.common.entitytype.EntityType;
import me.danetnaverno.editoni.common.world.*;
import me.danetnaverno.editoni.common.world.io.WorldIOProvider;
import me.danetnaverno.editoni.minecraft.world.*;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.DoubleTag;
import net.querz.nbt.ListTag;
import net.querz.nbt.NBTUtil;
import net.querz.nbt.mca.Chunk;
import net.querz.nbt.mca.MCAFile;
import net.querz.nbt.mca.MCAUtil;
import net.querz.nbt.mca.Section;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Minecraft114WorldIO implements WorldIOProvider
{
    private static Logger logger = LogManager.getLogger("Minecraft114WorldIO");
    private static Pattern mcaRegex = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca");

    @Override
    public boolean isAppropriateToRead(@NotNull Path path)
    {
        try
        {
            if (!Files.isDirectory(path))
                return false;

            Path levelDatFile = path.resolve("level.dat");
            if (Files.exists(levelDatFile))
            {
                CompoundTag levelDat = (CompoundTag) NBTUtil.readTag(levelDatFile.toFile());
                String versionName = levelDat.getCompoundTag("Data").getCompoundTag("Version").getString("Name");

                if (Integer.parseInt(versionName.split("\\.")[1]) >= 14)
                    return true;
            }
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    @Override
    public boolean isAppropriateToWrite(@NotNull World world)
    {
        return ((MinecraftWorld)world).version.startsWith("1.14");
    }

    @Override
    public World readWorld(@NotNull Path path) throws IOException
    {
        CompoundTag levelDat = (CompoundTag) NBTUtil.readTag(path.resolve("level.dat").toFile());
        String versionName = levelDat.getCompoundTag("Data").getCompoundTag("Version").getString("Name");
        MinecraftWorld world = new MinecraftWorld(versionName);
        Path regionFolder = path.resolve("region");

        for (File regionFile : Objects.requireNonNull(regionFolder.toFile().listFiles()))
        {
            Matcher matcher = mcaRegex.matcher(regionFile.getName());
            if (matcher.matches())
            {
                int x = Integer.parseInt(matcher.group(1));
                int z = Integer.parseInt(matcher.group(2));
                world.addRegion(readRegion(MCAUtil.readMCAFile(regionFile), x, z));
            }
        }
        return world;
    }

    private static MinecraftRegion readRegion(MCAFile mcaFile, int x, int z)
    {
        MinecraftRegion region = new MinecraftRegion(x, z);

        for (int renderX = 0; renderX < 32; renderX++)
            for (int renderZ = 0; renderZ < 32; renderZ++)
            {
                net.querz.nbt.mca.Chunk mcaChunk = mcaFile.getChunk(renderX, renderZ);
                if (mcaChunk != null)
                {
                    MinecraftChunk chunk = readChunk(mcaFile.getChunk(renderX, renderZ), renderX, renderZ);
                    region.setChunk(chunk);
                }
            }
        return region;
    }

    private static MinecraftChunk readChunk(net.querz.nbt.mca.Chunk mcaChunk, int renderX, int renderZ)
    {
        CompoundTag data = mcaChunk.data;

        int posX = data.getCompoundTag("Level").getInt("xPos");
        int posZ = data.getCompoundTag("Level").getInt("zPos");
        Map<Vector3i, Block> blocks = new HashMap<>();
        List<Entity> entities = new ArrayList<>();
        MinecraftChunk chunk = new MinecraftChunk(new MCAExtraInfo114(data), renderX, renderZ, posX, posZ, blocks, entities);

        //Entities
        for (CompoundTag tag : mcaChunk.getEntities())
        {
            EntityType type = EntityDictionary.getEntityType(new ResourceLocation(tag.getString("id")));
            ListTag<DoubleTag> posTag = (ListTag<DoubleTag>) tag.getListTag("Pos");
            Vector3d pos = new Vector3d(posTag.get(0).asDouble(), posTag.get(1).asDouble(), posTag.get(2).asDouble());
            entities.add(new MinecraftEntity(chunk, pos, type, tag));
        }

        //Tile Entities
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

        //Block States
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

    @Override
    public void writeWorld(@NotNull World world, @NotNull Path path) throws IOException
    {
        Path regionFolder = path.resolve("region");
        Files.createDirectories(regionFolder);
        MinecraftWorld mcWorld = (MinecraftWorld) world;
        for (MinecraftRegion region : mcWorld.getRegions())
            writeRegion(region, regionFolder.resolve("r." + region.x + "." + region.z + ".mca"));
    }

    private static void writeRegion(@NotNull MinecraftRegion region, @NotNull Path regionFile) throws IOException
    {
        MCAFile result = new MCAFile(region.x, region.z);
        for (MinecraftChunk chunk : region.getChunks())
            result.setChunk(chunk.getRenderX(), chunk.getRenderZ(), writeChunk(chunk));
        MCAUtil.writeMCAFile(result, regionFile.toFile());
    }

    private static Chunk writeChunk(@NotNull MinecraftChunk chunk)
    {
        ListTag<CompoundTag> tileEntities = new ListTag<>(CompoundTag.class);
        ListTag<CompoundTag> entities = new ListTag<>(CompoundTag.class);
        Chunk mcaChunk = new Chunk(chunk.getExtras().getData());

        for (Entity entity : chunk.getEntities())
            entities.add(entity.getTag());

        for (Block block : chunk.getBlocks())
        {
            CompoundTag properties = block.getState() != null ? block.getState().getTag() : null;
            CompoundTag blockState = new CompoundTag();
            blockState.putString("Name", block.getType().toString());
            if (properties != null)
                blockState.put("Properties", properties);
            mcaChunk.setBlockStateAt(block.getGlobalX(), block.getGlobalY(), block.getGlobalZ(), blockState, false);
            mcaChunk.setEntities(entities);

            CompoundTag tileEntity = block.getTileEntity() != null ? block.getTileEntity().getTag() : null;
            if (tileEntity != null)
                tileEntities.add(tileEntity);
        }
        mcaChunk.setTileEntities(tileEntities);

        for (int i = 0; i < 16; i++)
        {
            Section section = mcaChunk.getSection(i);
            if (section != null)
            {
                section.blockLight = null;
                section.skyLight = null;
            }
        }
        mcaChunk.updateHandle(chunk.getRenderX(), chunk.getRenderZ());
        return mcaChunk;
    }
}

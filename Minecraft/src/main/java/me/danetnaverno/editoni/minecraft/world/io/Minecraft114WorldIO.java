package me.danetnaverno.editoni.minecraft.world.io;

import me.danetnaverno.editoni.common.ResourceLocation;
import me.danetnaverno.editoni.common.blocktype.BlockDictionary;
import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.common.entitytype.EntityDictionary;
import me.danetnaverno.editoni.common.entitytype.EntityType;
import me.danetnaverno.editoni.common.world.BlockState;
import me.danetnaverno.editoni.common.world.BlockStateDictionary;
import me.danetnaverno.editoni.common.world.Entity;
import me.danetnaverno.editoni.common.world.World;
import me.danetnaverno.editoni.minecraft.util.location.LocationUtilsKt;
import me.danetnaverno.editoni.minecraft.util.location.RegionLocation;
import me.danetnaverno.editoni.minecraft.world.*;
import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import me.danetnaverno.editoni.util.location.EntityLocation;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.DoubleTag;
import net.querz.nbt.ListTag;
import net.querz.nbt.NBTUtil;
import net.querz.nbt.mca.Chunk;
import net.querz.nbt.mca.MCAFile;
import net.querz.nbt.mca.MCAUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Minecraft114WorldIO implements IMinecraftWorldIOProvider
{
    private static Logger logger = LogManager.getLogger("Minecraft114WorldIO");
    private static Pattern mcaRegex = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca");

    @Override
    public boolean isAppropriateToRead(@NotNull Path path)
    {
        try
        {
            if (path.getFileName().toString().equalsIgnoreCase("level.dat"))
                path = path.getParent();

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
    public World readWorld(@NotNull Path path) throws IOException
    {
        if (path.getFileName().toString().equalsIgnoreCase("level.dat"))
            path = path.getParent();
        CompoundTag levelDat = (CompoundTag) NBTUtil.readTag(path.resolve("level.dat").toFile());
        String versionName = levelDat.getCompoundTag("Data").getCompoundTag("Version").getString("Name");
        MinecraftWorld world = new MinecraftWorld(versionName, this);
        Path regionFolder = path.resolve("region");

        for (File regionFile : Objects.requireNonNull(regionFolder.toFile().listFiles()))
        {
            Matcher matcher = mcaRegex.matcher(regionFile.getName());
            if (matcher.matches())
            {
                int x = Integer.parseInt(matcher.group(1));
                int z = Integer.parseInt(matcher.group(2));
                world.addRegion(readRegion(world, regionFile.toPath(), x, z));
            }
        }
        return world;
    }

    private static MinecraftRegion readRegion(MinecraftWorld world, Path regionFile, int x, int z)
    {
        return new MinecraftRegion(world, regionFile, new RegionLocation(x, z));
    }

    @Override
    public void loadRegion(@NotNull MinecraftWorld world, @NotNull RegionLocation location) throws IOException
    {
        MinecraftRegion region = world.getRegion(location);
        MCAFile mcaFile = MCAUtil.readMCAFile(region.regionFile.toFile());
        for (int renderX = 0; renderX < 32; renderX++)
            for (int renderZ = 0; renderZ < 32; renderZ++)
            {
                net.querz.nbt.mca.Chunk mcaChunk = mcaFile.getChunk(renderX, renderZ);
                if (mcaChunk != null)
                {
                    MinecraftChunk chunk = readChunk(world, mcaFile.getChunk(renderX, renderZ), renderX, renderZ);
                    region.setChunk(chunk);
                }
            }
    }

    @Override
    public void loadChunk(@NotNull World world, @NotNull ChunkLocation location) throws IOException
    {
        MinecraftWorld mcWorld = (MinecraftWorld) world;
        MinecraftRegion region = mcWorld.getRegion(LocationUtilsKt.toRegionLocation(location));
        MCAFile mcaFile = MCAUtil.readMCAFile(region.regionFile.toFile());
        for (int renderX = 0; renderX < 32; renderX++)
            for (int renderZ = 0; renderZ < 32; renderZ++)
            {
                net.querz.nbt.mca.Chunk mcaChunk = mcaFile.getChunk(renderX, renderZ);
                if (mcaChunk != null)
                {
                    if (location.x == mcaChunk.data.getCompoundTag("Level").getInt("xPos")
                            && location.z == mcaChunk.data.getCompoundTag("Level").getInt("zPos"))
                    {
                        region.setChunk(readChunk(mcWorld, mcaFile.getChunk(renderX, renderZ), renderX, renderZ));
                        return;
                    }
                }
            }
    }

    private static MinecraftChunk readChunk(MinecraftWorld world, Chunk mcaChunk, int renderX, int renderZ)
    {
        CompoundTag data = mcaChunk.data;
        int posX = data.getCompoundTag("Level").getInt("xPos");
        int posZ = data.getCompoundTag("Level").getInt("zPos");

        BlockType[][] blockTypes = new BlockType[16][];
        Map<Integer, BlockState> blockStates = new HashMap<>();
        Map<Integer, MinecraftTileEntity> tileEntities = new HashMap<>();
        List<Entity> entities = new ArrayList<>();

        MinecraftChunk chunk = new MinecraftChunk(
                world, new ChunkLocation(posX, posZ), renderX, renderZ,
                new MCAExtraInfo114(data), entities);

        //Entities
        for (CompoundTag tag : mcaChunk.getEntities())
        {
            EntityType type = EntityDictionary.getEntityType(new ResourceLocation(tag.getString("id")));
            ListTag<DoubleTag> posTag = (ListTag<DoubleTag>) tag.getListTag("Pos");
            EntityLocation location = new EntityLocation(posTag.get(0).asDouble(), posTag.get(1).asDouble(), posTag.get(2).asDouble());
            entities.add(new MinecraftEntity(chunk, location, type, tag));
        }

        //Tile Entities
        for (CompoundTag tileEntity : mcaChunk.getTileEntities())
        {
            int globalX = tileEntity.getInt("x");
            int y = tileEntity.getInt("y");
            int globalZ = tileEntity.getInt("z");
            int x = globalX - (posX << 4);
            int z = globalZ - (posZ << 4);
            int index = LocationUtilsKt.toChunkBlockIndex(new BlockLocation(x, (byte) y, z));
            tileEntities.put(index, new MinecraftTileEntity(tileEntity));
        }

        //Block States
        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 256; y++)
                for (int z = 0; z < 16; z++)
                {
                    try
                    {
                        CompoundTag tag = mcaChunk.getBlockStateAt(x, y, z);
                        if (tag != null)
                        {
                            BlockType blockType = BlockDictionary.getBlockType(new ResourceLocation(tag.getString("Name")));
                            BlockState blockState = BlockStateDictionary.createBlockState(blockType, tag.getCompoundTag("Properties"));
                            BlockLocation location = new BlockLocation(chunk, x, y, z);
                            if (blockState != null)
                                blockStates.put(LocationUtilsKt.toChunkBlockIndex(location), blockState);
                            if (blockTypes[y / 16]==null)
                                blockTypes[y / 16] = new BlockType[4096];
                            blockTypes[y / 16][LocationUtilsKt.toSectionBlockIndex(location)] = blockType;
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
        chunk.load(blockTypes, blockStates, tileEntities);
        return chunk;
    }

    @Override
    public void writeWorld(@NotNull World world, @NotNull Path path) throws IOException
    {
        Path regionFolder = path.resolve("region");
        Files.createDirectories(regionFolder);
        MinecraftWorld mcWorld = (MinecraftWorld) world;
        for (MinecraftRegion region : mcWorld.getRegions())
        {
            writeRegion(region, regionFolder.resolve("r." + region.location.x + "." + region.location.z + ".mca"));
        }
    }

    private static void writeRegion(@NotNull MinecraftRegion region, @NotNull Path regionFile) throws IOException
    {
        MCAFile result = new MCAFile(region.location.x, region.location.z);
        for (MinecraftChunk chunk : region.getChunks())
            result.setChunk(chunk.renderX, chunk.renderZ, writeChunk(chunk));
        MCAUtil.writeMCAFile(result, regionFile.toFile());
    }

    private static Chunk writeChunk(@NotNull MinecraftChunk chunk)
    {
        throw new NotImplementedException();
        /*ListTag<CompoundTag> tileEntities = new ListTag<>(CompoundTag.class);
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
            mcaChunk.setBlockStateAt(block.getLocation().globalX, block.getLocation().globalY, block.getLocation().globalZ, blockState, false);
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
        mcaChunk.updateHandle(chunk.renderX, chunk.renderZ);
        return mcaChunk;*/
    }
}

package me.danetnaverno.editoni.world.world.io;

import me.danetnaverno.editoni.common.ResourceLocation;
import me.danetnaverno.editoni.common.blocktype.BlockDictionary;
import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.common.entitytype.EntityDictionary;
import me.danetnaverno.editoni.common.entitytype.EntityType;
import me.danetnaverno.editoni.common.world.*;
import me.danetnaverno.editoni.util.RobertoGarbagio;
import me.danetnaverno.editoni.util.location.BlockLocation;
import me.danetnaverno.editoni.util.location.ChunkLocation;
import me.danetnaverno.editoni.util.location.EntityLocation;
import me.danetnaverno.editoni.world.util.location.ChunkRegionOffset;
import me.danetnaverno.editoni.world.util.location.LocationUtilsKt;
import me.danetnaverno.editoni.world.util.location.RegionLocation;
import me.danetnaverno.editoni.world.world.*;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.DoubleTag;
import net.querz.nbt.ListTag;
import net.querz.nbt.mca.Chunk;
import net.querz.nbt.mca.MCAFile;
import net.querz.nbt.mca.MCAUtil;
import net.querz.nbt.mca.Section;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Minecraft114WorldIO implements IMinecraftWorldIOProvider
{
    private static Logger logger = LogManager.getLogger("Minecraft114WorldIO");

    private static Pattern mcaRegex = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca");

    private static Chunk writeChunk(@NotNull MinecraftChunk chunk)
    {
        ListTag<CompoundTag> tileEntities = new ListTag<>(CompoundTag.class);
        ListTag<CompoundTag> entities = new ListTag<>(CompoundTag.class);
        Chunk mcaChunk = new Chunk(chunk.getExtras().getData());

        for (Entity entity : chunk.getEntities())
            entities.add(entity.getTag());

        for (int x = 0; x < 16; x++)
            for (int y = 0; y < 256; y++)
                for (int z = 0; z < 16; z++)
                {
                    Block block = chunk.getBlockAt(new BlockLocation(chunk, x, y, z));
                    if (block == null)
                        continue;
                    CompoundTag properties = block.getState() != null ? block.getState().getTag() : null;
                    CompoundTag blockState = new CompoundTag();
                    blockState.putString("Name", block.getType().toString());
                    if (properties != null)
                        blockState.put("Properties", properties);
                    mcaChunk.setBlockStateAt(block.getLocation().globalX, block.getLocation().globalY, block.getLocation().globalZ, blockState, false);
                }
        mcaChunk.setEntities(entities);
        tileEntities.addAll(chunk.getTileEntities().values().stream().map(TileEntity::getTag).collect(Collectors.toList()));
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
        ChunkRegionOffset offset = LocationUtilsKt.toRegionOffset(chunk.location);
        mcaChunk.updateHandle(offset.x, offset.z);
        return mcaChunk;
    }

    @Override
    public boolean isAppropriateToRead(@NotNull Path path)
    {
        try
        {
            if (!Files.isDirectory(path))
                return false;

            int[] minVersion = {Integer.MAX_VALUE};
            int[] maxVersion = {0};

            Files.list(path).forEach(it -> {
                try
                {
                    MCAFile mcaFile = MCAUtil.readMCAFile(it.toFile());
                    for (int i = 0; i < 1024; i++)
                    {
                        Chunk chunk = mcaFile.getChunk(i);
                        if (chunk != null)
                        {
                            if (chunk.getDataVersion() < minVersion[0])
                                minVersion[0] = chunk.getDataVersion();
                            if (chunk.getDataVersion() > maxVersion[0])
                                maxVersion[0] = chunk.getDataVersion();
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
            return maxVersion[0] > 1947; //todo magic value 1.14 pre-1.
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    @Override
    public Collection<World> readWorlds(@NotNull Path path) throws IOException
    {
        MinecraftWorld world = new MinecraftWorld("v.todo", this, path);

        for (File regionFile : Objects.requireNonNull(path.toFile().listFiles()))
        {
            Matcher matcher = mcaRegex.matcher(regionFile.getName());
            if (matcher.matches())
            {
                int x = Integer.parseInt(matcher.group(1));
                int z = Integer.parseInt(matcher.group(2));
                world.addRegion(readRegion(world, regionFile.toPath(), x, z));
            }
        }
        return Collections.singletonList(world);
    }

    private static MinecraftRegion readRegion(MinecraftWorld world, Path regionFile, int x, int z) throws IOException
    {
        return new MinecraftRegion(MCAUtil.readMCAFile(regionFile.toFile()), world, new RegionLocation(x, z));
    }

    @Override
    public void loadRegion(@NotNull MinecraftWorld world, @NotNull RegionLocation location)
    {
        MinecraftRegion region = world.getRegion(location);
        for (int offsetX = 0; offsetX < 32; offsetX++)
            for (int offsetZ = 0; offsetZ < 32; offsetZ++)
            {
                net.querz.nbt.mca.Chunk mcaChunk = region.mcaFile.getChunk(offsetX, offsetZ);
                if (mcaChunk != null)
                {
                    if (region.getChunkIfLoaded(LocationUtilsKt.toChunkLocation(
                            new ChunkRegionOffset(offsetX, offsetZ), location)) == null)
                    {
                        MinecraftChunk chunk = readChunk(world, mcaChunk);
                        region.setChunk(chunk);
                    }
                }
            }
    }

    @Override
    public void loadChunk(@NotNull World world, @NotNull ChunkLocation location)
    {
        MinecraftWorld mcWorld = (MinecraftWorld) world;
        MinecraftRegion region = mcWorld.getRegion(LocationUtilsKt.toRegionLocation(location));
        ChunkRegionOffset offset = LocationUtilsKt.toRegionOffset(location);
        net.querz.nbt.mca.Chunk mcaChunk = region.mcaFile.getChunk(offset.x, offset.z);
        if (mcaChunk != null)
            region.setChunk(readChunk(mcWorld, mcaChunk));
    }

    private static MinecraftChunk readChunk(MinecraftWorld world, Chunk mcaChunk)
    {
        CompoundTag data = mcaChunk.data;
        int posX = data.getCompoundTag("Level").getInt("xPos");
        int posZ = data.getCompoundTag("Level").getInt("zPos");

        BlockType[][] blockTypes = new BlockType[16][16];
        Map<Integer, BlockState> blockStates = new HashMap<>();
        Map<Integer, TileEntity> tileEntities = new HashMap<>();
        List<Entity> entities = new ArrayList<>();

        MinecraftChunk chunk = new MinecraftChunk(
                world, new ChunkLocation(posX, posZ),
                new MCAExtraInfo114(data), entities);

        //Entities
        for (CompoundTag tag : mcaChunk.getEntities())
        {
            EntityType type = EntityDictionary.getEntityType(new ResourceLocation(tag.getString("id")));
            ListTag<DoubleTag> posTag = (ListTag<DoubleTag>) tag.getListTag("Pos");
            EntityLocation location = new EntityLocation(
                    posTag.get(0).asDouble(),
                    posTag.get(1).asDouble(),
                    posTag.get(2).asDouble());
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
                    CompoundTag tag = mcaChunk.getBlockStateAt(x, y, z);
                    if (tag != null)
                    {
                        BlockType blockType = BlockDictionary.getBlockType(new ResourceLocation(tag.getString("Name")));
                        BlockState blockState = BlockStateDictionary.createBlockState(blockType, tag.getCompoundTag("Properties"));
                        BlockLocation location = new BlockLocation(chunk, x, y, z);
                        if (blockState != null)
                            blockStates.put(LocationUtilsKt.toChunkBlockIndex(location), blockState);
                        if (blockTypes[y / 16] == null)
                            blockTypes[y / 16] = new BlockType[4096];
                        blockTypes[y / 16][LocationUtilsKt.toSectionBlockIndex(location)] = blockType;
                    }
                }
        chunk.load(blockTypes, blockStates, tileEntities);
        return chunk;
    }

    @Override
    public void writeWorld(@NotNull World world, @NotNull Path path) throws IOException
    {
        ForkJoinPool executor = new ForkJoinPool();
        long now = System.currentTimeMillis();
        Path regionFolder = path.resolve("region");
        Files.createDirectories(regionFolder);
        MinecraftWorld mcWorld = (MinecraftWorld) world;
        for (MinecraftRegion region : mcWorld.getRegions())
            executor.execute(() -> {
                try
                {
                    writeRegion(region, regionFolder.resolve("r." + region.location.x + "." + region.location.z + ".mca"));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
        try
        {
            executor.shutdown();
            executor.awaitTermination(9999, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        RobertoGarbagio.logger.info("Saved in " + (System.currentTimeMillis() - now));
    }

    private void writeRegion(@NotNull MinecraftRegion region, @NotNull Path regionFile) throws IOException
    {
        for (MinecraftChunk chunk : region.getLoadedChunks())
        {
            ChunkRegionOffset offset = LocationUtilsKt.toRegionOffset(chunk.location);
            region.mcaFile.setChunk(offset.x, offset.z, writeChunk(chunk));
        }
        MCAUtil.writeMCAFile(region.mcaFile, regionFile.toFile());
    }
}

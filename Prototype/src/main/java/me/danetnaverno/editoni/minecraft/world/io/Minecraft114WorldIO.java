package me.danetnaverno.editoni.minecraft.world.io;

import me.danetnaverno.editoni.common.ResourceLocation;
import me.danetnaverno.editoni.common.blocktype.BlockDictionary;
import me.danetnaverno.editoni.common.blocktype.BlockType;
import me.danetnaverno.editoni.common.entitytype.EntityDictionary;
import me.danetnaverno.editoni.common.entitytype.EntityType;
import me.danetnaverno.editoni.common.world.*;
import me.danetnaverno.editoni.minecraft.world.*;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.DoubleTag;
import net.querz.nbt.ListTag;
import net.querz.nbt.mca.Chunk;
import net.querz.nbt.mca.MCAFile;
import net.querz.nbt.mca.MCAUtil;
import net.querz.nbt.mca.Section;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Minecraft114WorldIO
{
    private static Pattern mcaRegex = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca");

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

    public static MinecraftRegion readRegion(MCAFile mcaFile, int x, int z)
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

    public static MinecraftChunk readChunk(net.querz.nbt.mca.Chunk mcaChunk, int renderX, int renderZ)
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

    public static void writeWorld(@NotNull World world, @NotNull File worldFolder) throws IOException
    {
        Files.createDirectories(worldFolder.toPath().resolve("region"));
        MinecraftWorld mcWorld = (MinecraftWorld) world;
        File regionFolder = new File(worldFolder, "region");
        for (MinecraftRegion region : mcWorld.getRegions())
            writeRegion(region, new File(regionFolder, "r." + region.x + "." + region.z + ".mca"));
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
        ListTag<CompoundTag> entities = new ListTag<>(CompoundTag.class);
        Chunk mcaChunk = new Chunk(chunk.extras.getData());

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

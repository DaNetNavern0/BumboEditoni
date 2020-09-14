package me.danetnaverno.editoni.io

import me.danetnaverno.editoni.blockstate.BlockStateDictionary
import me.danetnaverno.editoni.blocktype.BlockDictionary.getBlockType
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.editor.EditorTab
import me.danetnaverno.editoni.location.*
import me.danetnaverno.editoni.util.ResourceLocation
import me.danetnaverno.editoni.world.*
import net.querz.mca.LoadFlags
import net.querz.mca.MCAFile
import net.querz.mca.MCAUtil
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.regex.Pattern
import kotlin.reflect.jvm.isAccessible
import net.querz.mca.Chunk as QuerzChunk

class Minecraft114WorldIO
{
    fun isAppropriateToRead(path: Path): Boolean
    {
        try
        {
            if (!Files.isDirectory(path)) return false
            val minVersion = intArrayOf(Int.MAX_VALUE)
            val maxVersion = intArrayOf(0)
            Files.list(path).forEach {
                try
                {
                    val mcaFile: MCAFile = MCAUtil.read(it.toFile())
                    for (i in 0..1023)
                    {
                        val chunk = mcaFile.getChunk(i)
                        if (chunk != null)
                        {
                            if (chunk.dataVersion < minVersion[0])
                                minVersion[0] = chunk.dataVersion
                            if (chunk.dataVersion > maxVersion[0])
                                maxVersion[0] = chunk.dataVersion
                        }
                    }
                }
                catch (e: IOException)
                {
                    e.printStackTrace()
                }
            }
            return maxVersion[0] > 1947 //todo magic value 1.14 pre-1.
        }
        catch (ignored: Exception)
        {
        }
        return false
    }

    @Throws(IOException::class)
    fun readWorld(path: Path): World
    {
        val world = World("v.todo", this, path)
        for (regionFile in Objects.requireNonNull(path.toFile().listFiles()))
        {
            val matcher = mcaRegex.matcher(regionFile.name)
            if (matcher.matches())
            {
                val x = matcher.group(1).toInt()
                val z = matcher.group(2).toInt()
                world.addRegion(readRegion(world, regionFile.toPath(), x, z))
            }
        }
        return world
    }

    @Throws(IOException::class)
    private fun readRegion(world: World, regionFile: Path, x: Int, z: Int): Region
    {
        return Region(regionFile, world, RegionLocation(x, z))
    }

    fun readChunk(region: Region, location: IChunkLocation) : Chunk?
    {
        return readChunk(region, location.x, location.z)
    }

    fun readChunk(region: Region, globalX: Int, globalZ: Int) : Chunk?
    {
        val qChunk = readQChunk(region, globalX, globalZ) ?: return null
        return convertQChunk(region.world, qChunk)
    }

    @Throws(IOException::class)
    fun writeWorld(worldTab: EditorTab, path: Path)
    {
        val regionFolder = path.resolve("region")
        Files.createDirectories(regionFolder)

        worldTab.operationList.getAllTrulyAlteredChunks()
                .mapNotNull { worldTab.world.getChunk(it) }
                .forEach {
                    writeQChunk(regionFolder, it.region, composeQChunk(it), it.location.x, it.location.z)
                }

        /*val executor = ForkJoinPool()
        val regionFolder = path.resolve("region")
        Files.createDirectories(regionFolder)
        for (region in world.getRegions())
            executor.execute {
                try
                {
                    writeRegion(region, regionFolder.resolve("r." + region.location.x.toString() + "." + region.location.z.toString() + ".mca"))
                }
                catch (e: IOException)
                {
                    e.printStackTrace()
                }
            }
        try
        {
            executor.shutdown()
            executor.awaitTermination(9999, TimeUnit.SECONDS)
        }
        catch (e: InterruptedException)
        {
            e.printStackTrace()
        }*/
    }

    /*@Throws(IOException::class)
    private fun writeRegion(region: Region, regionFile: Path)
    {
        val mcaFile = MCAUtil.newMCAFile(regionFile.toFile())
        for (chunk in region.getLoadedChunks())
        {
            val (x, z) = chunk.location.toRegionOffset()
            mcaFile.setChunk(x, z, composeQChunk(chunk))
        }
        MCAUtil.write(mcaFile, regionFile.toFile())
    }*/


    //====================================
    //QuerzChunk-related methods
    //====================================

    private fun readQChunk(region: Region, globalX: Int, globalZ: Int): QuerzChunk?
    {
        RandomAccessFile(region.path.toFile(), "r").use { raf ->
            val index = MCAFile.getChunkIndex(globalX - region.chunkOffset.x, globalZ - region.chunkOffset.z)

            raf.seek(index * 4L)
            var offset = raf.read() shl 16
            offset = offset or (raf.read() and 0xFF shl 8)
            offset = offset or (raf.read() and 0xFF)
            if (raf.readByte().toInt() == 0)
                return null
            raf.seek(4096 + index * 4L)
            val timestamp = raf.readInt()
            val qChunk = newQChunk(timestamp)
            raf.seek(4096L * offset + 4L) //+4: skip data size
            qChunk.deserialize(raf, LoadFlags.ALL_DATA)
            return qChunk
        }
    }

    private fun convertQChunk(world: World, mcaChunk: QuerzChunk): Chunk
    {
        val data = getData(mcaChunk)
        val posX: Int = data.getCompoundTag("Level").getInt("xPos")
        val posZ: Int = data.getCompoundTag("Level").getInt("zPos")
        val blockTypes = arrayOfNulls<Array<BlockType?>?>(16)
        val blockStates: MutableMap<Int, BlockState> = HashMap<Int, BlockState>()
        val tileEntities: MutableMap<Int, TileEntity> = HashMap<Int, TileEntity>()
        val entities: MutableList<Entity> = ArrayList<Entity>()
        val chunk = Chunk(
                world, ChunkLocation(posX, posZ),
                MCAExtraInfo114(data), entities)

        //Entities
        /*for (tag in mcaChunk.getEntities())
        {
            val type = EntityDictionary.getEntityType(ResourceLocation(tag.getString("id")))
            val posTag: ListTag<DoubleTag> = tag.getListTag("Pos") as ListTag<DoubleTag>
            val location = EntityLocation(
                    posTag.get(0).asDouble(),
                    posTag.get(1).asDouble(),
                    posTag.get(2).asDouble())
            entities.add(MinecraftEntity(chunk, location, type, tag))
        }*/

        //Tile Entities
        for (tileEntity in mcaChunk.tileEntities)
        {
            val globalX: Int = tileEntity.getInt("x")
            val y: Int = tileEntity.getInt("y")
            val globalZ: Int = tileEntity.getInt("z")
            val x = globalX - (posX shl 4)
            val z = globalZ - (posZ shl 4)
            val index: Int = BlockLocation(x, y, z).toChunkBlockIndex()
            tileEntities[index] = TileEntity(tileEntity)
        }

        //Block States
        for (x in 0..15) for (y in 0..255) for (z in 0..15)
        {
            val tag = try
            {
                mcaChunk.getBlockStateAt(x, y, z)
            }
            catch (e: Exception) //todo bug within the thing
            {
                null
            }
            if (tag != null)
            {
                val blockType = getBlockType(ResourceLocation(tag.getString("Name")))
                val blockState = BlockStateDictionary.createBlockState(blockType, tag.getCompoundTag("Properties"))
                val location = BlockLocation(chunk, x, y, z)
                if (blockState != null)
                    blockStates[location.toChunkBlockIndex()] = blockState
                if (blockTypes[y / 16] == null)
                    blockTypes[y / 16] = arrayOfNulls(4096)
                blockTypes[y / 16]!![location.toSectionBlockIndex()] = blockType
            }
        }
        chunk.load(blockTypes, blockStates, tileEntities)
        return chunk
    }

    private fun writeQChunk(saveFolder: Path, region: Region, qChunk: QuerzChunk, globalX: Int, globalZ: Int)
    {
        val savePath = saveFolder.resolve(region.path.fileName)
        if (!Files.exists(savePath))
            return //todo

        //todo WIP
        RandomAccessFile(savePath.toFile(), "rw").use { raf ->
            var globalOffset = 2
            var lastWritten = 0
            val timestamp = (System.currentTimeMillis() / 1000L).toInt()
            var chunksWritten = 0

            val index = MCAFile.getChunkIndex(globalX - region.chunkOffset.x, globalZ - region.chunkOffset.z) + 1
            raf.seek(4096L * globalOffset * index)
            qChunk.serialize(raf, globalX, globalZ)
        }
    }

    private fun composeQChunk(chunk: Chunk): QuerzChunk
    {
        val tileEntities: ListTag<CompoundTag> = ListTag(CompoundTag::class.java)
        val entities: ListTag<CompoundTag> = ListTag(CompoundTag::class.java)
        val mcaChunk = QuerzChunk(chunk.extras.data)
        for (entity in chunk.getEntities()) entities.add(entity.tag)
        for (x in 0..15) for (y in 0..255) for (z in 0..15)
        {
            val block = chunk.getBlockAt(BlockLocation(chunk, x, y, z)) ?: continue
            val properties = if (block.state != null) block.state.tag else null
            val blockState = CompoundTag()
            blockState.putString("Name", block.type.toString())
            if (properties != null) blockState.put("Properties", properties)
            mcaChunk.setBlockStateAt(block.location.globalX, block.location.globalY, block.location.globalZ, blockState, false)
        }
        mcaChunk.entities = entities
        tileEntities.addAll(chunk.tileEntities.values.map { it.tag })
        mcaChunk.tileEntities = tileEntities
        for (i in 0..15)
        {
            val section = mcaChunk.getSection(i)
            if (section != null)
            {
                section.blockLight = null
                section.skyLight = null
            }
        }
        val (x, z) = chunk.location.toRegionOffset()
        mcaChunk.updateHandle(x, z)
        return mcaChunk
    }

    companion object
    {
        private val mcaRegex = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca")
        private val dataField = QuerzChunk::class.java.getDeclaredField("data")
        private val qChunkConstr = QuerzChunk::class.constructors.first()

        init
        {
            qChunkConstr.isAccessible = true
            dataField.isAccessible = true
        }

        fun getData(chunk: QuerzChunk): CompoundTag
        {
            return dataField.get(chunk) as CompoundTag
        }

        fun newQChunk(timestamp: Int): QuerzChunk
        {
            return qChunkConstr.call(timestamp)
        }
    }
}
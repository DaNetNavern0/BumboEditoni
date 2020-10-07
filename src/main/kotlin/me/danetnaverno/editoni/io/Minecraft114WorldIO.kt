package me.danetnaverno.editoni.io

import me.danetnaverno.editoni.blockstate.BlockState
import me.danetnaverno.editoni.blockstate.BlockStateDictionary
import me.danetnaverno.editoni.blocktype.BlockDictionary
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.location.RegionLocation
import me.danetnaverno.editoni.world.*
import net.querz.mca.LoadFlags
import net.querz.mca.MCAFile
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.regex.Pattern
import kotlin.reflect.jvm.isAccessible
import net.querz.mca.Chunk as QuerzChunk

/**
 * todo A huge disclaimer - this class in in heavy development - saving doesn't work, the idea of how it's going to work isn't solidified etc
 */
class Minecraft114WorldIO : IMinecraftWorldIO
{
    override fun isAppropriateToRead(path: Path): Boolean
    {
        TODO()
        /*try
        {
            if (!Files.isDirectory(path))
                return false
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
        return false*/
    }

    //====================================
    // World reading
    //====================================

    override fun openWorld(path: Path): World
    {
        val world = World("v.todo", this, path)
        for (regionFile in Objects.requireNonNull(path.toFile().listFiles()))
        {
            val matcher = mcaRegex.matcher(regionFile.name)
            if (matcher.matches())
            {
                val x = matcher.group(1).toInt()
                val z = matcher.group(2).toInt()
                world.addRegion(openRegion(world, regionFile.toPath(), x, z))
            }
        }
        return world
    }

    private fun openRegion(world: World, regionFile: Path, x: Int, z: Int): Region
    {
        return Region(regionFile, world, RegionLocation(x, z))
    }

    override fun readChunk(region: Region, globalX: Int, globalZ: Int): Chunk?
    {
        val qChunk = readQChunkFromFile(region, globalX, globalZ) ?: return null
        return convertFromQChunk(region.world, qChunk)
    }

    //====================================
    // World writing
    //====================================

    override fun writeWorld(world: World, targetPath: Path)
    {
        val regionFolder = targetPath.resolve("region")
        Files.createDirectories(regionFolder)

        world.operationList.getAllTrulyAlteredChunks().asSequence()
                .mapNotNull { world.getChunk(it) }
                .forEach {
                    writeQChunkToFile(regionFolder, it.region, composeQChunk(it), it.location.x, it.location.z)
                }
    }


    //====================================
    //QuerzChunk-related methods
    //====================================

    private fun readQChunkFromFile(region: Region, globalX: Int, globalZ: Int): QuerzChunk?
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

    private fun convertFromQChunk(world: World, mcaChunk: QuerzChunk): Chunk
    {
        val data = getData(mcaChunk)
        val posX = data.getCompoundTag("Level").getInt("xPos")
        val posZ = data.getCompoundTag("Level").getInt("zPos")
        val blockTypes = arrayOfNulls<Array<BlockType?>?>(16)
        val blockStates = HashMap<Int, BlockState>()
        val tileEntities = HashMap<Int, TileEntity>()
        val entities = ArrayList<Entity>()
        val chunk = Chunk(world, ChunkLocation(posX, posZ), MCAExtraInfo114(data), entities)

        //Entities
        /*for (tag in mcaChunk.getEntities())
        {
            val type = EntityDictionary.getEntityType(tag.getString("id"))
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
            val globalX = tileEntity.getInt("x")
            val y = tileEntity.getInt("y")
            val globalZ = tileEntity.getInt("z")
            val x = globalX - (posX shl 4)
            val z = globalZ - (posZ shl 4)
            tileEntities[y * 256 + z * 16 + x] = TileEntity(tileEntity)
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
                val blockType = BlockDictionary.getBlockType(tag.getString("Name"))
                val blockState = BlockStateDictionary.createBlockState(blockType, tag.getCompoundTag("Properties"))
                if (blockState != null)
                    blockStates[y * 256 + z * 16 + x] = blockState //chunkIndex formula
                if (blockTypes[y / 16] == null)
                    blockTypes[y / 16] = arrayOfNulls(4096)
                blockTypes[y / 16]!![(y % 16) * 256 + z * 16 + x] = blockType //sectionIndex formula
            }
        }
        chunk.load(blockTypes, blockStates, tileEntities)
        return chunk
    }

    private fun writeQChunkToFile(saveFolder: Path, region: Region, qChunk: QuerzChunk, globalX: Int, globalZ: Int)
    {
        val savePath = saveFolder.resolve(region.path.fileName)
        if (!Files.exists(savePath))
            return //todo

        TODO()
        /*RandomAccessFile(savePath.toFile(), "rw").use { raf ->
            var globalOffset = 2
            var lastWritten = 0
            val timestamp = (System.currentTimeMillis() / 1000L).toInt()
            var chunksWritten = 0

            val index = MCAFile.getChunkIndex(globalX - region.chunkOffset.x, globalZ - region.chunkOffset.z) + 1
            raf.seek(4096L * globalOffset * index)
            qChunk.serialize(raf, globalX, globalZ)
        }*/
    }

    private fun composeQChunk(chunk: Chunk): QuerzChunk
    {
        val tileEntities: ListTag<CompoundTag> = ListTag(CompoundTag::class.java)
        val entities: ListTag<CompoundTag> = ListTag(CompoundTag::class.java)
        val mcaChunk = QuerzChunk(chunk.extras.data)
        for (entity in chunk.getEntities()) entities.add(entity.tag)
        for (x in 0..15) for (y in 0..255) for (z in 0..15)
        {
            //todo maybe we should replace getBlockAt with something that doesn't create a short-living [Block] object
            val block = chunk.getBlockAt(BlockLocation(chunk, x, y, z)) ?: continue
            val properties = if (block.state != null)
                block.state.tag
            else
                null
            val blockState = CompoundTag()
            blockState.putString("Name", block.type.toString())
            if (properties != null)
                blockState.put("Properties", properties)
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

        mcaChunk.updateHandle(chunk.location.x, chunk.location.z)
        return mcaChunk
    }

    private companion object
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
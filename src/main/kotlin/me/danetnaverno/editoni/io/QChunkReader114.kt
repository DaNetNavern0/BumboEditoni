package me.danetnaverno.editoni.io

import me.danetnaverno.editoni.blockstate.BlockState
import me.danetnaverno.editoni.blockstate.BlockStateDictionary
import me.danetnaverno.editoni.blocktype.BlockDictionary
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.util.await
import me.danetnaverno.editoni.world.Entity
import me.danetnaverno.editoni.world.Region
import me.danetnaverno.editoni.world.TileEntity
import me.danetnaverno.editoni.world.World
import net.querz.mca.LoadFlags
import net.querz.mca.MCAFile
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import java.io.RandomAccessFile
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaConstructor
import me.danetnaverno.editoni.world.Chunk as BumboChunk
import net.querz.mca.Chunk as QuerzChunk

object QChunkReader114 : IQChunkReader
{
    val fileReadingExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private val dataField: MethodHandle
    private val qChunkConstructor: MethodHandle

    init
    {
        fileReadingExecutor.submit { Thread.currentThread().name = "FileReadingExecutor" }
        val lookup = MethodHandles.lookup()

        val qChunkConstructorRef = QuerzChunk::class.constructors.first()
        qChunkConstructorRef.isAccessible = true
        qChunkConstructor = lookup.unreflectConstructor(qChunkConstructorRef.javaConstructor)

        val qDataField = QuerzChunk::class.java.getDeclaredField("data")
        qDataField.isAccessible = true
        dataField = lookup.unreflectGetter(qDataField)
    }

    fun getData(chunk: QuerzChunk): CompoundTag
    {
        return dataField.invokeExact(chunk) as CompoundTag
    }

    fun newQChunk(timestamp: Int): QuerzChunk
    {
        return qChunkConstructor.invokeExact(timestamp) as QuerzChunk
    }


    internal fun convertFromQChunk(world: World, mcaChunk: QuerzChunk): BumboChunk
    {
        val data = getData(mcaChunk)
        val posX = data.getCompoundTag("Level").getInt("xPos")
        val posZ = data.getCompoundTag("Level").getInt("zPos")
        val blockTypes = arrayOfNulls<Array<BlockType?>?>(16)
        val blockStates = HashMap<Int, BlockState>()
        val tileEntities = HashMap<Int, TileEntity>()
        val entities = ArrayList<Entity>()
        val chunk = BumboChunk(world, ChunkLocation(posX, posZ), MCAExtraInfo114(data), entities)

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

    internal fun writeQChunkToFile(saveFolder: Path, region: Region, qChunk: QuerzChunk, globalX: Int, globalZ: Int)
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

    internal fun composeQChunk(chunk: BumboChunk): QuerzChunk
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
            mcaChunk.setBlockStateAt(block.blockLocation.globalX, block.blockLocation.globalY, block.blockLocation.globalZ, blockState, false)
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

        mcaChunk.updateHandle(chunk.chunkLocation.x, chunk.chunkLocation.z)
        return mcaChunk
    }

    suspend fun readQChunkFromFile(region: Region, globalX: Int, globalZ: Int): QuerzChunk?
    {
        val future = CompletableFuture<QuerzChunk?>()
        fileReadingExecutor.submit {
            RandomAccessFile(region.path.toFile(), "r").use { raf ->
                val index = MCAFile.getChunkIndex(globalX - region.chunkOffset.x, globalZ - region.chunkOffset.z)

                raf.seek(index * 4L)
                var offset = raf.read() shl 16
                offset = offset or (raf.read() and 0xFF shl 8)
                offset = offset or (raf.read() and 0xFF)
                if (raf.readByte().toInt() == 0)
                    future.complete(null)
                raf.seek(4096 + index * 4L)
                val timestamp = raf.readInt()
                val qChunk = newQChunk(timestamp)
                raf.seek(4096L * offset + 4L) //+4: skip data size
                qChunk.deserialize(raf, LoadFlags.ALL_DATA)
                future.complete(qChunk)
            }
        }
        return future.await()
    }
}
package me.danetnaverno.editoni.io

import me.danetnaverno.editoni.blockstate.BlockState
import me.danetnaverno.editoni.blockstate.BlockStateDictionary
import me.danetnaverno.editoni.blocktype.BlockDictionary
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.util.BumboChunk
import me.danetnaverno.editoni.util.QuerzChunk
import me.danetnaverno.editoni.world.Entity
import me.danetnaverno.editoni.world.Region
import me.danetnaverno.editoni.world.TileEntity
import me.danetnaverno.editoni.world.World
import net.querz.mca.LoadFlags
import net.querz.mca.MCAFile
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object QChunkReader114 : QChunkReader()
{
    override fun convertFromQChunk(world: World, qChunk: QuerzChunk): BumboChunk?
    {
        val data = getData(qChunk)
        val posX = data.getCompoundTag("Level").getInt("xPos")
        val posZ = data.getCompoundTag("Level").getInt("zPos")
        val blockTypes = arrayOfNulls<Array<BlockType?>?>(16)
        val blockStates = HashMap<Int, BlockState>()
        val tileEntities = HashMap<Int, TileEntity>()
        val entities = ArrayList<Entity>()
        val chunk = BumboChunk(world, ChunkLocation(posX, posZ), MCAExtraInfo114(data), entities)

        //Entities
        /*for (tag in qChunk.getEntities())
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
        for (tileEntity in qChunk.tileEntities)
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
                qChunk.getBlockStateAt(x, y, z)
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

    override fun writeQChunkToFile(saveFolder: Path, region: Region, qChunk: QuerzChunk, globalX: Int, globalZ: Int)
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

    override fun composeQChunk(chunk: BumboChunk): QuerzChunk
    {
        val tileEntities: ListTag<CompoundTag> = ListTag(CompoundTag::class.java)
        val entities: ListTag<CompoundTag> = ListTag(CompoundTag::class.java)
        val qChunk = QuerzChunk(chunk.extras.data)
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
            qChunk.setBlockStateAt(block.blockLocation.globalX, block.blockLocation.globalY, block.blockLocation.globalZ, blockState, false)
        }
        qChunk.entities = entities
        tileEntities.addAll(chunk.tileEntities.values.map { it.tag })
        qChunk.tileEntities = tileEntities
        for (i in 0..15)
        {
            val section = qChunk.getSection(i)
            if (section != null)
            {
                section.blockLight = null
                section.skyLight = null
            }
        }

        qChunk.updateHandle(chunk.chunkLocation.x, chunk.chunkLocation.z)
        return qChunk
    }

    override fun readQChunkFromFileInner(region: Region, globalX: Int, globalZ: Int): QuerzChunk?
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
}
package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.MinecraftDictionaryFiller
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.io.Minecraft114WorldIO
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import me.danetnaverno.editoni.util.location.EntityLocation
import me.danetnaverno.editoni.util.location.toRegionLocation
import me.danetnaverno.editoni.util.location.RegionLocation
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList

class World public constructor(var version: String, var worldIOProvider: Minecraft114WorldIO, var path: Path)
{
    lateinit var worldRenderer: WorldRenderer

    private val regions = HashMap<RegionLocation, Region>()

    //======================
    // REGIONS
    //======================
    fun getRegions(): Collection<Region>
    {
        return regions.values
    }

    fun getRegion(location: RegionLocation): Region?
    {
        return regions.get(location)
    }

    fun addRegion(region: Region)
    {
        regions[region.location] = region
    }

    //======================
// CHUNKS
//======================
    fun getLoadedChunks(): List<Chunk>
    {
        return regions.values.flatMap { it.getLoadedChunks() }
    }

    fun getChunkIfLoaded(location: ChunkLocation): Chunk?
    {
        val region = getRegion(location.toRegionLocation()) ?: return null
        return region.getChunkIfLoaded(location)
    }

    fun getChunk(location: ChunkLocation): Chunk?
    {
        val region = getRegion(location.toRegionLocation()) ?: return null
        return region.getChunk(location)
    }

    fun loadChunkAt(chunkLocation: ChunkLocation)
    {
        getRegion(chunkLocation.toRegionLocation())!!.loadChunkAt(chunkLocation) //todo
    }

    fun unloadChunks(chunksToUnload: List<Chunk>)
    {
        for (chunk in chunksToUnload)
            getRegion(chunk.location.toRegionLocation())!!.unloadChunk(chunk.location) //todo
    }

    fun createChunk(location: ChunkLocation): Chunk
    {
        throw NotImplementedException()
    }

    //======================
    // BLOCKS
    //======================
    fun getBlockAt(location: BlockLocation): Block?
    {
        val chunk = getChunk(location.toChunkLocation()) ?: return null
        return chunk.getBlockAt(location)
    }

    fun getBlockTypeAt(location: BlockLocation): BlockType?
    {
        val chunk = getChunk(location.toChunkLocation()) ?: return null
        return chunk.getBlockTypeAt(location)
    }

    fun getBlockStateAt(location: BlockLocation): BlockState?
    {
        val chunk = getChunk(location.toChunkLocation()) ?: return null
        return chunk.getBlockStateAt(location)
    }

    fun getTileEntityAt(location: BlockLocation): TileEntity?
    {
        val chunk = getChunk(location.toChunkLocation()) ?: return null
        return chunk.getTileEntityAt(location)
    }

    fun getLoadedBlockAt(location: BlockLocation): Block?
    {
        val chunk = getChunkIfLoaded(location.toChunkLocation()) ?: return null
        return chunk.getBlockAt(location)
    }

    fun getLoadedBlockTypeAt(location: BlockLocation): BlockType?
    {
        val chunk = getChunkIfLoaded(location.toChunkLocation()) ?: return null
        return chunk.getBlockTypeAt(location)
    }

    fun getLoadedBlockStateAt(location: BlockLocation): BlockState?
    {
        val chunk = getChunkIfLoaded(location.toChunkLocation()) ?: return null
        return chunk.getBlockStateAt(location)
    }

    fun getLoadedTileEntityAt(location: BlockLocation): TileEntity?
    {
        val chunk = getChunkIfLoaded(location.toChunkLocation()) ?: return null
        return chunk.getTileEntityAt(location)
    }

    fun setBlock(block: Block)
    {
        var chunk = getChunk(block.location.toChunkLocation())
        if (chunk == null) chunk = createChunk(block.location.toChunkLocation())
        chunk.setBlock(block)
    }

    fun deleteBlock(location: BlockLocation)
    {
        val chunk = getChunk(location.toChunkLocation())
        if (chunk != null) setBlock(Block(chunk, location, MinecraftDictionaryFiller.AIR, null, null))
    }


    //======================
    // ENTITIES
    //======================
    fun getEntitiesAt(location: EntityLocation, radius: Float): List<Entity>?
    {
        return ArrayList(); //todo
        /*return getLoadedChunks().stream()
                .filter { chunk: Chunk -> chunk.location.distance(location.toChunkLocation()) <= 1 }
                .flatMap<Any> { chunk: Chunk -> chunk.getEntitiesAt(location, radius).stream() }
                .sorted(Comparator.comparingDouble { entity: Any -> entity.getLocation().distanceSquared(location) })
                .collect(Collectors.toList<Any>())*/
    }

    //======================
    // UTIL
    //======================

    override fun toString(): String
    {
        try
        {
            if (path.parent.fileName.toString().contains("DIM1")) return path.parent.parent.fileName.toString() + " (End; " + version + ")"
            if (path.parent.fileName.toString().contains("DIM-1")) return path.parent.parent.fileName.toString() + " (Nether; " + version + ")"
            if (path.fileName.toString().contains("region")) return path.parent.fileName.toString() + " (" + version + ")"
        }
        catch (e: Exception)
        {
            return path.fileName.toString() + " (" + version + ")"
        }
        return path.fileName.toString() + " (" + version + ")"
    }
}
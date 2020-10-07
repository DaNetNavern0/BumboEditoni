package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.MinecraftDictionaryFiller
import me.danetnaverno.editoni.blockstate.BlockState
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.editor.Settings
import me.danetnaverno.editoni.io.IMinecraftWorldIO
import me.danetnaverno.editoni.location.*
import me.danetnaverno.editoni.operation.OperationList
import me.danetnaverno.editoni.render.WorldRenderer
import org.joml.Vector3f
import org.joml.Vector3i
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.floor

class World constructor(val version: String, val worldIO: IMinecraftWorldIO, val path: Path)
{
    val worldRenderer = WorldRenderer(this)
    val operationList = OperationList(this)

    /**
     * [RegionLocation.equals]/[RegionLocationMutable.equals] is rather slow, because it involves casting,
     * and [HashMap] calls equals(Any?), rather than equals(RegionLocation)/equals(RegionLocationMutable).
     *
     * todo this gets called in extremely large amounts and has to be optimized. Probably with a custom map implementation.
     *   Creating a method like [IRegionLocation].toLong() and trying to use that would help, but not enough,
     *   because then we'd create a [Long] wrapper every time we interact with this map
     */
    private val regions = HashMap<IRegionLocation, Region>()

    //todo It's not great having this field being public
    val loadedChunksCache = ArrayList<Chunk>(Settings.renderDistance * Settings.renderDistance * 4)

    //======================
    // REGIONS
    //======================
    fun getRegions(): Collection<Region>
    {
        return regions.values
    }

    fun getRegion(location: IRegionLocation): Region?
    {
        return regions[location]
    }

    fun addRegion(region: Region)
    {
        regions[region.location] = region
    }

    //======================
    // CHUNKS
    //======================

    fun loadChunkAsync(chunkLocation: IChunkLocation, ticket: ChunkTicket)
    {
        getRegion(chunkLocation.toRegionLocation())?.loadChunkAsync(chunkLocation, ticket)
    }

    fun loadChunkSync(chunkLocation: IChunkLocation, ticket: ChunkTicket): Chunk?
    {
        return getRegion(chunkLocation.toRegionLocation())?.loadChunkSync(chunkLocation, ticket)
    }

    fun getLoadedChunks(): List<Chunk>
    {
        return loadedChunksCache
    }

    fun getChunk(location: IBlockLocation): Chunk?
    {
        val region = getRegion(location.toRegionLocation()) ?: return null
        return region.getChunk(location.toChunkLocation())
    }

    fun getChunk(location: IChunkLocation): Chunk?
    {
        val region = getRegion(location.toRegionLocation()) ?: return null
        return region.getChunk(location)
    }

    fun unloadChunk(chunk: Chunk)
    {
        getRegion(chunk.location.toRegionLocation())?.unloadChunk(chunk)
    }

    fun unloadChunks(chunks: Collection<Chunk>)
    {
        for (chunk in chunks)
            getRegion(chunk.location.toRegionLocation())!!.unloadChunk(chunk)
    }

    fun createChunk(location: ChunkLocation): Chunk
    {
        throw NotImplementedException()
    }

    //======================
    // BLOCKS
    //======================
    fun getBlockAt(location: IBlockLocation): Block?
    {
        val chunk = getChunk(location) ?: return null
        return chunk.getBlockAt(location)
    }

    fun getBlockTypeAt(location: IBlockLocation): BlockType?
    {
        val chunk = getChunk(location) ?: return null
        return chunk.getBlockTypeAt(location)
    }

    fun getBlockStateAt(location: IBlockLocation): BlockState?
    {
        val chunk = getChunk(location) ?: return null
        return chunk.getBlockStateAt(location)
    }

    fun getTileEntityAt(location: IBlockLocation): TileEntity?
    {
        val chunk = getChunk(location) ?: return null
        return chunk.getTileEntityAt(location)
    }


    fun setBlock(block: Block)
    {
        var chunk = getChunk(block.location)
        if (chunk == null)
            chunk = createChunk(block.location.toChunkLocation())
        chunk.setBlock(block)
    }

    fun deleteBlock(location: IBlockLocation)
    {
        val chunk = getChunk(location)
        if (chunk != null)
            setBlock(Block(chunk, location.toImmutable(), MinecraftDictionaryFiller.AIR, null, null))
    }

    fun findBlock(point: Vector3f): Block?
    {
        if (point.y < 0 || point.y > 255)
            return null
        val floor = Vector3i(floor(point.x).toInt() - 1, floor(point.y).toInt() - 1, floor(point.z).toInt() - 1)
        val ceiling = Vector3i(ceil(point.x).toInt() + 1, ceil(point.y).toInt() + 1, ceil(point.z).toInt() + 1)

        var closest: Block? = null
        var min = Double.MAX_VALUE

        for (x in floor.x..ceiling.x)
            for (y in floor.y..ceiling.y)
                for (z in floor.z..ceiling.z)
                {
                    val dx = point.x - (x + 0.5)
                    val dy = point.y - (y + 0.5)
                    val dz = point.z - (z + 0.5)
                    val distance = dx * dx + dy * dy + dz * dz
                    if (distance < min)
                    {
                        val block = this.getBlockAt(BlockLocation(x, y, z))
                        if (block != null && !block.type.isHidden /*&& !hiddenBlocks.contains(block.location)*/)
                        {
                            closest = block
                            min = distance
                        }
                    }
                }
        return closest
    }


    //======================
    // ENTITIES
    //======================
    fun getEntitiesAt(location: EntityLocation, radius: Float): List<Entity>?
    {
        return ArrayList() //todo
        /*return getLoadedChunks().stream()
                .filter { chunk: Chunk -> chunk.location.distance(location.toChunkLocation()) <= 1 }
                .flatMap<Any> { chunk: Chunk -> chunk.getEntitiesAt(location, radius).stream() }
                .sorted(Comparator.comparingDouble { entity: Any -> entity.getLocation().distanceSquared(location) })
                .collect(Collectors.toList<Any>())*/
    }

    fun findEntity(location: EntityLocation): Entity?
    {
        return getEntitiesAt(location.add(0.0, -0.5, 0.0), 1f)?.firstOrNull()
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
package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import me.danetnaverno.editoni.world.util.location.RegionLocation
import net.querz.mca.MCAFile


class Region(@JvmField val mcaFile: MCAFile, @JvmField val world: World, @JvmField val location: RegionLocation)
{
    private val chunks = HashMap<ChunkLocation, Chunk>()

    fun loadAllChunks()
    {
        world.worldIOProvider.loadRegion(world, location)
    }

    fun loadChunkAt(chunkLocation: ChunkLocation)
    {
        if (!chunks.containsKey(chunkLocation))
            world.worldIOProvider.loadChunk(world, chunkLocation)
    }

    fun unloadChunk(chunkLocation: ChunkLocation)
    {
        chunks.remove(chunkLocation)
    }

    fun getLoadedChunks(): Collection<Chunk>
    {
        return chunks.values.toList()
    }

    fun getAllChunks(): Collection<Chunk>
    {
        loadAllChunks()
        return chunks.values.toList()
    }

    fun getChunkIfLoaded(location: ChunkLocation): Chunk?
    {
        return chunks[location]
    }

    fun getChunk(location: ChunkLocation): Chunk?
    {
        loadChunkAt(location)
        return chunks[location]
    }

    fun getChunk(location: BlockLocation): Chunk?
    {
        return getChunk(location.toChunkLocation())
    }

    fun setChunk(chunk: Chunk)
    {
        chunks[chunk.location] = chunk
    }
}
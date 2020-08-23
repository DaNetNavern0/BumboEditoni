package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.location.RegionLocation
import net.querz.mca.MCAFile


class Region(@JvmField val mcaFile: MCAFile, @JvmField val world: World, @JvmField val location: RegionLocation)
{
    private val chunks = Array<Array<Chunk?>>(32) { arrayOfNulls(32) }
    private val chunkOffset = ChunkLocation(location.x shl 5, location.z shl 5)

    fun loadAllChunks()
    {
        world.worldIOProvider.loadRegion(world, location)
    }

    fun loadChunkAt(chunkLocation: ChunkLocation, ticket: ChunkTicket) : Chunk?
    {
        if (chunks[chunkLocation.x - chunkOffset.x][chunkLocation.z - chunkOffset.z] == null)
            return world.worldIOProvider.loadChunk(world, chunkLocation, ticket)
        return null
    }

    fun unloadChunk(chunkLocation: ChunkLocation)
    {
        chunks[chunkLocation.x - chunkOffset.x][chunkLocation.z - chunkOffset.z] = null
    }

    fun getLoadedChunks(): Collection<Chunk>
    {
        val result = arrayListOf<Chunk>()
        for (i in 0 until 32)
            for (j in 0 until 32)
        {
            val chunk = chunks[i][j]
            if (chunk != null)
                result.add(chunk)
        }
        return result
    }

    fun getAllChunks(): Collection<Chunk>
    {
        loadAllChunks()
        return getLoadedChunks()
    }


    fun getChunkIfLoaded(location: ChunkLocation): Chunk?
    {
        return chunks[location.x - chunkOffset.x][location.z - chunkOffset.z]
    }

    fun getChunk(location: ChunkLocation): Chunk?
    {
        return getChunkIfLoaded(location)
    }

    fun setChunk(chunk: Chunk) : Chunk
    {
        chunks[chunk.location.x - chunkOffset.x][chunk.location.z - chunkOffset.z] = chunk
        return chunk
    }
}
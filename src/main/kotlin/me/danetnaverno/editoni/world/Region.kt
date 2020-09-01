package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.location.RegionLocation
import java.io.RandomAccessFile

class Region(@JvmField val file: RandomAccessFile, @JvmField val world: World, @JvmField val location: RegionLocation)
{
    private val chunks = Array<Array<Chunk?>>(32) { arrayOfNulls(32) }
    val chunkOffset = ChunkLocation(location.x shl 5, location.z shl 5)

    fun loadChunkAt(chunkLocation: ChunkLocation, ticket: ChunkTicket) : Chunk?
    {
        require(this.location.isChunkLocationBelongs(chunkLocation)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.location} chunkLocation=${chunkLocation}"
        }
        val localX = chunkLocation.x - chunkOffset.x
        val localZ = chunkLocation.z - chunkOffset.z
        if (chunks[localX][localZ] == null)
        {
            val chunk = world.worldIOProvider.readChunk(this, chunkLocation) ?: return null
            chunks[localX][localZ] = chunk
            ChunkTicketManager.addTicket(chunk, ticket)
            return chunk
        }
        return null
    }

    fun unloadChunk(chunk: Chunk)
    {
        require(this.location.isChunkLocationBelongs(chunk.location)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.location} chunkLocation=${chunk.location}"
        }
        chunks[chunk.location.x - chunkOffset.x][chunk.location.z - chunkOffset.z] = null
        ChunkTicketManager.clearTickets(chunk)
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

    fun getChunk(location: ChunkLocation): Chunk?
    {
        require(this.location.isChunkLocationBelongs(location)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.location} chunkLocation=${location}"
        }
        return chunks[location.x - chunkOffset.x][location.z - chunkOffset.z]
    }

    fun setChunk(chunk: Chunk) : Chunk
    {
        require(this.location.isChunkLocationBelongs(chunk.location)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.location} chunkLocation=${chunk.location}"
        }
        chunks[chunk.location.x - chunkOffset.x][chunk.location.z - chunkOffset.z] = chunk
        return chunk
    }
}
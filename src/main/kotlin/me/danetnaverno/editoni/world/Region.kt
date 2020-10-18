package me.danetnaverno.editoni.world

import kotlinx.coroutines.launch
import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.location.IChunkLocation
import me.danetnaverno.editoni.location.RegionLocation
import me.danetnaverno.editoni.util.ChunkDataProcessingScope
import me.danetnaverno.editoni.util.MainThreadScope
import java.nio.file.Path

class Region(val path: Path, val world: World, val regionLocation: RegionLocation)
{
    private val chunks = Array(32) { arrayOfNulls<Any?>(32) }

    val chunkOffset = ChunkLocation(regionLocation.x shl 5, regionLocation.z shl 5)

    fun loadChunkAsync(chunkLocation: IChunkLocation, ticket: ChunkTicket)
    {
        require(this.regionLocation.isChunkLocationBelongs(chunkLocation)) {
            "ChunkLocation is out of region boundaries: regionLocation=${this.regionLocation} chunkLocation=${chunkLocation}"
        }
        val x = chunkLocation.x
        val z = chunkLocation.z
        val localX = x - chunkOffset.x
        val localZ = z - chunkOffset.z
        if (chunks[localX][localZ] == null)
        {
            chunks[localX][localZ] = Chunk.Placeholder
            ChunkDataProcessingScope.launch {
                val chunk = world.worldIO.readChunk(this@Region, x, z) ?: return@launch
                MainThreadScope.launch {
                    chunks[localX][localZ] = chunk
                    ChunkManager.addTicket(chunk, ticket)
                    world.loadedChunksCache.add(chunk)
                    world.worldRenderer.markChunkToBake(chunk)
                }
            }

        }
    }

    fun unloadChunk(chunk: Chunk)
    {
        require(this.regionLocation.isChunkLocationBelongs(chunk.chunkLocation)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.regionLocation} chunkLocation=${chunk.chunkLocation}"
        }
        val dx = chunk.chunkLocation.x - chunkOffset.x
        val dz = chunk.chunkLocation.z - chunkOffset.z
        chunk.renderer.invalidate()
        chunks[dx][dz] = null
        ChunkManager.clearTickets(chunk)
        world.loadedChunksCache.remove(chunk)
    }

    fun getLoadedChunks(): Collection<Chunk>
    {
        val result = ArrayList<Chunk>(32)
        for (i in 0 until 32)
            for (j in 0 until 32)
            {
                val chunk = chunks[i][j]
                if (chunk is Chunk)
                    result.add(chunk)
            }
        return result
    }

    fun getLoadedChunks(outResult: MutableCollection<Chunk>)
    {
        for (i in 0 until 32)
            for (j in 0 until 32)
            {
                val chunk = chunks[i][j]
                if (chunk is Chunk)
                    outResult.add(chunk)
            }
    }

    fun getChunk(chunkLocation: IChunkLocation): Chunk?
    {
        require(this.regionLocation.isChunkLocationBelongs(chunkLocation)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.regionLocation} chunkLocation=${chunkLocation}"
        }
        return chunks[chunkLocation.x - chunkOffset.x][chunkLocation.z - chunkOffset.z] as? Chunk
    }

    fun setChunk(chunk: Chunk): Chunk
    {
        require(this.regionLocation.isChunkLocationBelongs(chunk.chunkLocation)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.regionLocation} chunkLocation=${chunk.chunkLocation}"
        }
        chunks[chunk.chunkLocation.x - chunkOffset.x][chunk.chunkLocation.z - chunkOffset.z] = chunk
        return chunk
    }
}
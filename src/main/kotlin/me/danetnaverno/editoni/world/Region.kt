package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.editor.EditorApplication
import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.location.IChunkLocation
import me.danetnaverno.editoni.location.RegionLocation
import java.nio.file.Path

class Region(val path: Path, val world: World, val regionLocation: RegionLocation)
{
    private val chunks = Array<Array<Any?>>(32) { arrayOfNulls(32) }

    val chunkOffset = ChunkLocation(regionLocation.x shl 5, regionLocation.z shl 5)

    fun loadChunkAsync(chunkLocation: IChunkLocation, ticket: ChunkTicket)
    {
        require(this.regionLocation.isChunkLocationBelongs(chunkLocation)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.regionLocation} chunkLocation=${chunkLocation}"
        }
        val x = chunkLocation.x
        val z = chunkLocation.z
        val localX = x - chunkOffset.x
        val localZ = z - chunkOffset.z
        if (chunks[localX][localZ] == null)
        {
            chunks[localX][localZ] = Chunk.Placeholder
            ChunkManager.chunkLoadingExecutor.execute {
                val chunk = world.worldIO.readChunk(this, x, z)
                if (chunk != null)
                {
                    // todo I'm really not sure about this thing. It opens endless possibilities for multiple types of pasta
                    EditorApplication.mainThreadExecutor.addTask {
                        chunks[localX][localZ] = chunk
                        ChunkManager.addTicket(chunk, ticket)
                        world.loadedChunksCache.add(chunk)
                        world.worldRenderer.bakeChunk(chunk)
                    }
                }
            }
        }
    }

    fun loadChunkSync(chunkLocation: IChunkLocation, ticket: ChunkTicket): Chunk?
    {
        require(this.regionLocation.isChunkLocationBelongs(chunkLocation)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.regionLocation} chunkLocation=${chunkLocation}"
        }
        val localX = chunkLocation.x - chunkOffset.x
        val localZ = chunkLocation.z - chunkOffset.z
        if (chunks[localX][localZ] == null)
        {
            val chunk = world.worldIO.readChunk(this, chunkLocation) ?: return null
            chunks[localX][localZ] = chunk
            ChunkManager.addTicket(chunk, ticket)
            world.loadedChunksCache.add(chunk)
            world.worldRenderer.bakeChunk(chunk)
            return chunk
        }
        return null
    }

    fun unloadChunk(chunk: Chunk)
    {
        require(this.regionLocation.isChunkLocationBelongs(chunk.chunkLocation)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.regionLocation} chunkLocation=${chunk.chunkLocation}"
        }
        val dx = chunk.chunkLocation.x - chunkOffset.x
        val dz = chunk.chunkLocation.z - chunkOffset.z
        chunk.vertexData.invalidate()
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
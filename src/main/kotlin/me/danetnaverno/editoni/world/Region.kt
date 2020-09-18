package me.danetnaverno.editoni.world

import com.sun.jmx.remote.internal.ArrayQueue
import me.danetnaverno.editoni.editor.EditorApplication
import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.location.IChunkLocation
import me.danetnaverno.editoni.location.RegionLocation
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList

class Region(@JvmField val path: Path, @JvmField val world: World, @JvmField val location: RegionLocation)
{
    private val chunks = Array<Array<Any?>>(32) { arrayOfNulls(32) }

    val chunkOffset = ChunkLocation(location.x shl 5, location.z shl 5)

    fun loadChunkAsync(chunkLocation: IChunkLocation, ticket: ChunkTicket)
    {
        require(this.location.isChunkLocationBelongs(chunkLocation)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.location} chunkLocation=${chunkLocation}"
        }
        val x = chunkLocation.x
        val z = chunkLocation.z
        val localX = x - chunkOffset.x
        val localZ = z - chunkOffset.z
        if (chunks[localX][localZ] == null)
        {
            chunks[localX][localZ] = Chunk.Placeholder
            ChunkManager.chunkLoadingExecutor.execute {
                val chunk = world.worldIOProvider.readChunk(this, x, z)
                if (chunk != null)
                    EditorApplication.mainThreadExecutor.addTask {
                        chunks[localX][localZ] = chunk
                        ChunkManager.addTicket(chunk, ticket)
                        //todo this isn't particularly nice. Because we don't want visible blocks on chunk boundaries
                        // we invalidate chunks around a newly loaded one, which makes tons of updates when we load
                        // chunks on masses (e.g. opening a new world)
                        for (ix in -1 .. +1)
                            for (iz in -1 .. +1)
                                world.getChunk(ChunkLocation(x + ix,z + iz))?.vertexData?.invalidate()
                    }
            }
        }
    }

    fun loadChunkAt(chunkLocation: ChunkLocation, ticket: ChunkTicket): Chunk?
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
            ChunkManager.addTicket(chunk, ticket)
            //todo this isn't particularly nice. Because we don't want visible blocks on chunk boundaries
            // we invalidate chunks around a newly loaded one, which makes tons of updates when we load
            // chunks on masses (e.g. opening a new world)
            for (ix in -1..+1)
                for (iz in -1..+1)
                    world.getChunk(ChunkLocation(chunkLocation.x + ix, chunkLocation.z + iz))?.vertexData?.invalidate()
            return chunk
        }
        return null
    }

    fun unloadChunk(chunk: Chunk)
    {
        require(this.location.isChunkLocationBelongs(chunk.location)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.location} chunkLocation=${chunk.location}"
        }
        val dx = chunk.location.x - chunkOffset.x
        val dz = chunk.location.z - chunkOffset.z
        chunk.vertexData.invalidate()
        chunks[dx][dz] = null
        ChunkManager.clearTickets(chunk)
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

    fun getChunk(location: IChunkLocation): Chunk?
    {
        require(this.location.isChunkLocationBelongs(location)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.location} chunkLocation=${location}"
        }
        return chunks[location.x - chunkOffset.x][location.z - chunkOffset.z] as? Chunk
    }

    fun setChunk(chunk: Chunk): Chunk
    {
        require(this.location.isChunkLocationBelongs(chunk.location)) {
            "ChunkLocation is out of region  boundaries: regionLocation=${this.location} chunkLocation=${chunk.location}"
        }
        chunks[chunk.location.x - chunkOffset.x][chunk.location.z - chunkOffset.z] = chunk
        return chunk
    }
}
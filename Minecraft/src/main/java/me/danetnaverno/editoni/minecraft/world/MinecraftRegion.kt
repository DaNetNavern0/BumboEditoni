package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.minecraft.world.io.Minecraft114WorldIO
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList

class MinecraftRegion(val world: MinecraftWorld, val regionFile: Path, val x: Int, val z: Int)
{
    private val chunks = HashMap<ChunkLocation, MinecraftChunk>()

    fun loadAllChunks()
    {
        Minecraft114WorldIO.loadRegion(world, this)
    }

    fun loadChunkAt(chunkLocation: ChunkLocation)
    {
        if (!chunks.containsKey(chunkLocation))
            Minecraft114WorldIO.loadChunk(world, this, chunkLocation)
    }

    fun getLoadedChunks(): Collection<MinecraftChunk>
    {
        return ArrayList(chunks.values)
    }

    fun getChunks(): Collection<MinecraftChunk>
    {
        loadAllChunks()
        return ArrayList(chunks.values)
    }

    fun getChunkIfLoaded(location: ChunkLocation): MinecraftChunk?
    {
        return chunks[location]
    }

    fun getChunk(location: ChunkLocation): MinecraftChunk?
    {
        loadChunkAt(location)
        return chunks[location]
    }

    fun getChunk(location: BlockLocation): MinecraftChunk?
    {
        Calendar.getInstance()
        return getChunk(location.toChunkLocation())
    }

    fun setChunk(chunk: MinecraftChunk)
    {
        chunks[chunk.location] = chunk
    }
}
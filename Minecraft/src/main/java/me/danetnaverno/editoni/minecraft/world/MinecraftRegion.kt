package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.minecraft.util.location.RegionLocation
import me.danetnaverno.editoni.minecraft.world.io.IMinecraftWorldIOProvider
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList

class MinecraftRegion(@JvmField val world: MinecraftWorld, @JvmField val regionFile: Path, @JvmField val location: RegionLocation)
{
    private val chunks = HashMap<ChunkLocation, MinecraftChunk>()

    fun loadAllChunks()
    {
        (world.worldIOProvider as IMinecraftWorldIOProvider).loadRegion(world, location)
    }

    fun loadChunkAt(chunkLocation: ChunkLocation)
    {
        if (!chunks.containsKey(chunkLocation))
            world.worldIOProvider.loadChunk(world, chunkLocation)
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
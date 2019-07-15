package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.minecraft.world.io.Minecraft114WorldIO
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList

class MinecraftRegion(val world: MinecraftWorld, val regionFile: Path, val x: Int, val z: Int)
{
    var isLoaded: Boolean = false
        private set

    private val chunks = HashMap<ChunkLocation, MinecraftChunk>()

    fun load()
    {
        if (!isLoaded)
        {
            isLoaded = true
            Minecraft114WorldIO.loadRegion(world, this)
        }
    }

    fun getChunks(): Collection<MinecraftChunk>
    {
        load()
        return ArrayList(chunks.values)
    }

    fun getChunk(location: ChunkLocation): MinecraftChunk?
    {
        load()
        return chunks[location]
    }

    fun getChunk(location: BlockLocation): MinecraftChunk?
    {
        return getChunk(location.toChunkLocation())
    }

    fun setChunk(chunk: MinecraftChunk)
    {
        chunks[chunk.location] = chunk
    }
}
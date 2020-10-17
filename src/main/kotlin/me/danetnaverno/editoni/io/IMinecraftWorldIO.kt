package me.danetnaverno.editoni.io

import me.danetnaverno.editoni.location.IChunkLocation
import me.danetnaverno.editoni.world.Chunk
import me.danetnaverno.editoni.world.Region
import me.danetnaverno.editoni.world.World
import java.nio.file.Path

interface IMinecraftWorldIO
{
    fun isAppropriateToRead(path: Path): Boolean

    fun openWorld(path: Path): World

    suspend fun readChunk(region: Region, chunkLocation: IChunkLocation): Chunk?
    {
        return readChunk(region, chunkLocation.x, chunkLocation.z)
    }

    suspend fun readChunk(region: Region, globalX: Int, globalZ: Int): Chunk?

    fun writeWorld(world: World, targetPath: Path)

    companion object
    {
        fun getWorldIO(path: Path): IMinecraftWorldIO
        {
            return Minecraft114WorldIO(QChunkReader114) //todo
        }
    }
}

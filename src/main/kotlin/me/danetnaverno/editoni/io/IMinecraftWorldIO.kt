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

    fun readChunk(region: Region, location: IChunkLocation): Chunk?
    {
        return readChunk(region, location.x, location.z)
    }

    fun readChunk(region: Region, globalX: Int, globalZ: Int): Chunk?

    fun writeWorld(world: World, targetPath: Path)
}

package me.danetnaverno.editoni.io

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.location.RegionLocation
import me.danetnaverno.editoni.world.Chunk
import me.danetnaverno.editoni.world.Region
import me.danetnaverno.editoni.world.World
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

/**
 * todo A huge disclaimer - this class in in heavy development - saving doesn't work, the idea of how it's going to work isn't solidified etc
 */
class Minecraft114WorldIO(private val qChunkReader: QChunkReader114) : IMinecraftWorldIO
{
    override fun isAppropriateToRead(path: Path): Boolean
    {
        TODO()
    }

    //====================================
    // World reading
    //====================================

    override fun openWorld(path: Path): World
    {
        //todo this method doesn't account for very huge worlds
        // we need a way to introduce regions on the fly, rather than declaring all of them prematurely
        val world = World("v.todo", this, path)
        Files.walk(path).forEach {
            val matcher = mcaRegex.matcher(it.fileName.toString())
            if (matcher.matches())
            {
                val x = matcher.group(1).toInt()
                val z = matcher.group(2).toInt()
                world.addRegion(openRegion(world, it, x, z))
            }
        }
        return world
    }

    private fun openRegion(world: World, regionFile: Path, x: Int, z: Int): Region
    {
        return Region(regionFile, world, RegionLocation(x, z))
    }

    override suspend fun readChunk(region: Region, globalX: Int, globalZ: Int): Chunk?
    {
        val qChunk = qChunkReader.readQChunkFromFile(region, globalX, globalZ) ?: return null
        return qChunkReader.convertFromQChunk(region.world, qChunk)
    }

    //====================================
    // World writing
    //====================================

    override fun writeWorld(world: World, targetPath: Path)
    {
        val regionFolder = targetPath.resolve("region")
        Files.createDirectories(regionFolder)

        Editor.getTab(world).operationList.getAllTrulyAlteredChunks().asSequence()
                .mapNotNull { world.getChunk(it) }
                .forEach {
                    qChunkReader.writeQChunkToFile(regionFolder, it.region, qChunkReader.composeQChunk(it), it.chunkLocation.x, it.chunkLocation.z)
                }
    }


    private companion object
    {
        private val mcaRegex = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca")
    }
}
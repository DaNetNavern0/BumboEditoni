package me.danetnaverno.editoni.minecraft.world.io

import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.common.world.io.WorldIO
import me.danetnaverno.editoni.minecraft.util.location.RegionLocation
import me.danetnaverno.editoni.minecraft.world.MinecraftWorld
import me.danetnaverno.editoni.util.location.ChunkLocation
import net.querz.nbt.CompoundTag
import net.querz.nbt.NBTUtil
import java.nio.file.Files
import java.nio.file.Path

class MinecraftLevelDatIO : IMinecraftWorldIOProvider
{
    override fun isAppropriateToRead(path: Path): Boolean
    {
        try
        {

            val levelDatFile = (if (path.endsWith("level.dat")) path else path.resolve("level.dat")) ?: return false
            if (Files.exists(levelDatFile))
            {
                val levelDat = NBTUtil.readTag(levelDatFile.toFile()) as CompoundTag
                val versionName = levelDat.getCompoundTag("Data").getCompoundTag("Version").getString("Name")

                if (Integer.parseInt(versionName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]) >= 14)
                    return true
            }
        }
        catch (ignored: Exception)
        {
        }

        return false
    }

    override fun readWorlds(ogPath: Path): Collection<World>
    {
        var path = ogPath
        if (path.fileName.toString().equals("level.dat", ignoreCase = true))
            path = path.parent

        val worlds = mutableListOf<World>()
        worlds += WorldIO.readWorlds(path.resolve("region"))

        val netherFolder = path.resolve("DIM-1/region")
        if (Files.exists(netherFolder) && Files.list(netherFolder).count() > 0)
            worlds += WorldIO.readWorlds(netherFolder)

        val endFolder = path.resolve("DIM1/region")
        if (Files.exists(endFolder) && Files.list(endFolder).count() > 0)
            worlds += WorldIO.readWorlds(endFolder)

        return worlds
    }

    override fun writeWorld(world: World, path: Path)
    {
        throw IllegalStateException("MinecraftLevelDatIO isn't supposed to be used to save worlds. Probably a programming error.")
    }

    override fun loadChunk(world: World, location: ChunkLocation)
    {
        throw IllegalStateException("MinecraftLevelDatIO isn't supposed to be used to load chunks. Probably a programming error.")
    }

    override fun loadRegion(world: MinecraftWorld, location: RegionLocation)
    {
        throw IllegalStateException("MinecraftLevelDatIO isn't supposed to be used to load regions. Probably a programming error.")
    }
}

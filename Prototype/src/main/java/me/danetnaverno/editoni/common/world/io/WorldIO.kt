package me.danetnaverno.editoni.common.world.io

import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.common.world.io.WorldIO.WorldIOException.Problem.NOT_A_FILE
import me.danetnaverno.editoni.common.world.io.WorldIO.WorldIOException.Problem.NOT_RECOGNIZED
import me.danetnaverno.editoni.minecraft.world.io.Minecraft113WorldIO
import net.querz.nbt.CompoundTag
import net.querz.nbt.NBTUtil
import java.io.File

object WorldIO
{
    fun readWorld(worldFolder: File): World
    {
        if (!worldFolder.isDirectory)
            throw WorldIOException("'$worldFolder' is not a folder", NOT_A_FILE)
        if (worldFolder.listFiles().any { it.name.equals("level.dat", true) })
        {
            val levelDat = NBTUtil.readTag(File(worldFolder, "level.dat")) as CompoundTag
            val versionName = levelDat.getCompoundTag("Data").getCompoundTag("Version").getString("Name")

            if (versionName.split(".")[1].toInt() >= 13)
                return Minecraft113WorldIO.readWorld(worldFolder)

            throw WorldIOException("Game version of the world '$worldFolder' is too old ($versionName). Update it to version 1.13 or newer", NOT_RECOGNIZED)
        }
        throw WorldIOException("World not found in '$worldFolder'", NOT_RECOGNIZED)
    }

    fun writeWorld(world: World, worldFolder: File, savingMethod: SavingMethod)
    {
        if (savingMethod==SavingMethod.MC113)
            Minecraft113WorldIO.writeWorld(world, worldFolder)
        else if (savingMethod==SavingMethod.MC114)
            Minecraft113WorldIO.writeWorld114(world, worldFolder)
    }

    class WorldIOException(message: String, problem: Problem) : Exception(message)
    {
        enum class Problem
        {
            NOT_RECOGNIZED, NOT_A_FILE, OLD_LEVEL_VERSION, NBT_ERROR
        }
    }

    enum class SavingMethod
    {
        MC113, MC114
    }
}
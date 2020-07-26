package me.danetnaverno.editoni.editor

import me.danetnaverno.editoni.MinecraftDictionaryFiller
import me.danetnaverno.editoni.io.MCAExtraInfo
import me.danetnaverno.editoni.io.Minecraft114WorldIO
import me.danetnaverno.editoni.util.Camera
import me.danetnaverno.editoni.util.location.ChunkLocation
import me.danetnaverno.editoni.world.Chunk
import me.danetnaverno.editoni.world.Region
import me.danetnaverno.editoni.world.World
import me.danetnaverno.editoni.world.WorldRenderer
import me.danetnaverno.editoni.world.util.location.RegionLocation
import net.querz.mca.MCAFile
import net.querz.nbt.tag.CompoundTag
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL11
import java.nio.file.Path
import java.nio.file.Paths

object Editor
{
    val logger = LogManager.getLogger("Editor")!!
    var renderDistance = 3

    lateinit var currentWorld: World;

    val worlds = mutableMapOf<Path, World>()

    init
    {
        InputHandler.init(EditorApplication.getWindowId())
        try
        {
            Camera.x = 0.0
            Camera.y = 20.0
            Camera.z = 0.0
            Camera.yaw = -150.0
            Camera.pitch = -20.0
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun loadWorld(worldPath: Path): World
    {
        val loadedWorld = worlds[worldPath]
        if (loadedWorld == null)
        {
            val loadedWorlds = Minecraft114WorldIO().readWorlds(worldPath) //todo
            if (loadedWorlds.isEmpty())
                return currentWorld
            for (world in loadedWorlds)
                worlds[world.path] = world
            return loadedWorlds.first()
        }
        return loadedWorld
    }

    fun placeholder()
    {
        var chunk = Chunk(currentWorld, ChunkLocation(0, 0), MCAExtraInfo(CompoundTag()), ArrayList())
        chunk.load(arrayOfNulls(16), mutableMapOf(), mutableMapOf())
        val region = Region(MCAFile(0, 0), currentWorld, RegionLocation(0, 0))
        MinecraftDictionaryFiller.init()

        region.setChunk(chunk)
        chunk = Chunk(currentWorld, ChunkLocation(0, 1), MCAExtraInfo(CompoundTag()), ArrayList())
        chunk.load(arrayOfNulls(16), mutableMapOf(), mutableMapOf())
        region.setChunk(chunk)
        chunk = Chunk(currentWorld, ChunkLocation(1, 0), MCAExtraInfo(CompoundTag()), ArrayList())
        chunk.load(arrayOfNulls(16), mutableMapOf(), mutableMapOf())
        region.setChunk(chunk)
        chunk = Chunk(currentWorld, ChunkLocation(1, 1), MCAExtraInfo(CompoundTag()), ArrayList())
        chunk.load(arrayOfNulls(16), mutableMapOf(), mutableMapOf())
        region.setChunk(chunk)

        currentWorld.addRegion(region)
        currentWorld.worldRenderer = WorldRenderer(currentWorld)
    }

    fun displayLoop()
    {
        GL11.glRotated(Camera.pitch, -1.0, 0.0, 0.0)
        GL11.glRotated(Camera.yaw, 0.0, -1.0, 0.0)
        GL11.glTranslated(-Camera.x, -Camera.y, -Camera.z)

        currentWorld.worldRenderer.render()

        EditorUserHandler.selections()
        EditorUserHandler.controls()
        InputHandler.update()
    }
}
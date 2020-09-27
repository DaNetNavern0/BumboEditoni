package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.editor.EditorApplication
import me.danetnaverno.editoni.editor.Settings
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Predicate

object ChunkManager
{
    val chunkLoadingExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val tickets: MutableMap<Chunk, MutableSet<ChunkTicket>> = ConcurrentHashMap()

    init
    {
        chunkLoadingExecutor.scheduleAtFixedRate({
            EditorApplication.mainThreadExecutor.addTask { unloadExcessChunks() }
        }, 0, Settings.chunkCleanupPeriod, TimeUnit.SECONDS)
    }


    fun addTicket(chunk: Chunk, chunkTicket: ChunkTicket)
    {
        var set = tickets[chunk]
        if (set == null)
        {
            set = mutableSetOf()
            tickets[chunk] = set
        }
        //Yes, the existence of a dummy [ChunkTicketCamera] isn't particularly great, but it's better than having alternative
        //  [loadChunk/loadChunkAsync] version without a ticket, or having to make a ticket manually after loading a chunk,
        //  because any potential programmer trying to build upon this system will have to make a legit ticket anyway.
        if (chunkTicket != ChunkTicketCamera)
            set.add(chunkTicket)
    }

    fun removeTicket(chunk: Chunk, chunkTicket: ChunkTicket)
    {
        tickets[chunk]?.remove(chunkTicket)
    }

    fun removeTicketsIf(chunk: Chunk, predicate: Predicate<ChunkTicket>)
    {
        tickets[chunk]?.removeIf(predicate)
    }

    fun clearTickets(chunk: Chunk)
    {
        tickets.remove(chunk)
    }

    fun isLoadedByCamera(chunk: Chunk): Boolean
    {
        val cameraLocation = chunk.world.editorTab.camera.mutableLocation.toChunkLocation()
        return chunk.location.withinCubicDistance(cameraLocation, Settings.chunkLoadDistance)
    }

    fun unloadExcessChunks()
    {
        for (value in Editor.worlds)
        {
            unloadExcessChunks(value)
        }
    }

    fun unloadExcessChunks(world: World)
    {
        world.unloadChunks(getExcessChunks(world))
    }

    fun getExcessChunks(world: World) : List<Chunk>
    {
        return world.getLoadedChunks().filter {
            val list = tickets[it]
            (list == null || list.isEmpty()) && !isLoadedByCamera(it)
        }
    }
}
package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.editor.EditorApplication
import me.danetnaverno.editoni.editor.Settings
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Predicate

object ChunkManager
{
    val chunkLoadingExecutor = Executors.newSingleThreadScheduledExecutor()
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

    fun unloadExcessChunks()
    {
        for (world in Editor.tabs.map { it.value.world })
            unloadExcessChunks(world)
    }

    fun isLoadedByCamera(chunk: Chunk): Boolean
    {
        val tab = Editor.tabs.values.first { it.world == chunk.world }
        val cameraLocation = tab.camera.mutableLocation.toChunkLocation()
        return chunk.location.withinCubicDistance(cameraLocation, Settings.renderDistance)
    }

    fun unloadExcessChunks(world: World)
    {
        world.getLoadedChunks().forEach {
            val list = tickets[it]
            val noTickets = list == null || list.isEmpty()
            if (noTickets && !isLoadedByCamera(it))
                world.unloadChunk(it)
        }
    }
}
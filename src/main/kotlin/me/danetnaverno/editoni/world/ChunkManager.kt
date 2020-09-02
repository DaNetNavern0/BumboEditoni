package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.editor.EditorApplication
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
        }, 0, 10, TimeUnit.SECONDS) //todo multithreading issues all over the place
    }

    fun addTicket(chunk: Chunk, chunkTicket: ChunkTicket)
    {
        var set = tickets[chunk]
        if (set == null)
        {
            set = mutableSetOf()
            tickets[chunk] = set
        }
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

    fun cleanCameraTickets()
    {
        for (ticket in tickets.values)
            ticket.remove(ChunkTicketCamera)
    }

    fun unloadExcessChunks()
    {
        for (world in Editor.tabs.map { it.value.world })
            unloadExcessChunks(world)
    }

    fun unloadExcessChunks(world: World)
    {
        tickets.entries.removeIf {
            val noTickets = it.value.isEmpty()
            if (noTickets)
                world.unloadChunk(it.key)
            noTickets
        }
    }
}
package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.operations.Operation
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object ChunkTicketManager
{
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val tickets: MutableMap<Chunk, MutableSet<ChunkTicket>> = ConcurrentHashMap()

    init
    {
        executor.scheduleAtFixedRate({ unloadExcessChunks() }, 0, 10, TimeUnit.SECONDS)
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

    fun cleanCameraTickets(world: World)
    {
        val cameraTicket = ChunkTicketCamera()
        for (ticket in tickets.values)
            ticket.remove(cameraTicket)
    }

    fun unloadExcessChunks()
    {
        for (world in Editor.tabs.map { it.value.world })
            unloadExcessChunks(world)
    }

    fun unloadExcessChunks(world: World)
    {
        tickets.entries.removeIf {
            val anyTickets = !tickets.values.isEmpty()
            if (anyTickets)
                world.unloadChunk(it.key) //todo sync queue... or _A_sync?
            anyTickets
        }
    }
}
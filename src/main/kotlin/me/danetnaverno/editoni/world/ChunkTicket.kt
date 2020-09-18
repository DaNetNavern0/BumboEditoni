package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.operations.Operation

open class ChunkTicket

object ChunkTicketCamera : ChunkTicket()

class ChunkTicketOperation(val operation: Operation) : ChunkTicket()
{
    override fun equals(other: Any?): Boolean
    {
        return other is ChunkTicketOperation && other.operation == operation
    }

    override fun hashCode(): Int
    {
        return operation.hashCode()
    }
}

class ChunkTicketForceLoad : ChunkTicket()
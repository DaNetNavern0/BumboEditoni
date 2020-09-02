package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.operations.Operation

open class ChunkTicket

/**
 * Since we can have only one camera per world, being loaded by the camera is binary:
 *  the camera is either close enough to load a chunk, or not.
 * You can't have multiple tickets from multiple cameras being stacked
 */
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
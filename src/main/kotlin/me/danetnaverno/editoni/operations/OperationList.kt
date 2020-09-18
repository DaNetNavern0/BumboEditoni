package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.editor.EditorGUI.refreshOperationHistory
import me.danetnaverno.editoni.editor.EditorTab
import me.danetnaverno.editoni.editor.Settings
import me.danetnaverno.editoni.location.ChunkArea
import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.util.Translation.translate
import me.danetnaverno.editoni.world.ChunkManager
import me.danetnaverno.editoni.world.ChunkTicketOperation
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*
import javax.swing.JOptionPane

class OperationList constructor(val editorTab: EditorTab)
{
    var savePosition = 0
    private val operationList: MutableList<Operation> = ArrayList()
    private var currentPosition = 0

    fun apply(operation: Operation)
    {
        operation.operationList = this
        if (currentPosition >= operationList.size - 1)
        {
            val isObserving = operation is IObservingOperation
            if (!isObserving || !Settings.hideObservingOperations)
                operationList.add(operation)
            currentPosition = operationList.size - 1
            operation.initialApply()
            val chunks = operation.alteredChunks
            if (chunks != null)
            {
                redrawChunkArea(chunks)
                if (!isObserving)
                    chunks.iterator().asSequence()
                            .mapNotNull { editorTab.world.getChunk(it) }
                            .forEach { ChunkManager.addTicket(it, ChunkTicketOperation(operation)) }
            }
        }
        else
        {
            val dialogButton = JOptionPane.showConfirmDialog(null,
                    translate("operation.confirm_forced_operation", operationList.size - currentPosition),
                    "", JOptionPane.YES_NO_OPTION)
            if (dialogButton == JOptionPane.YES_OPTION) applyForced(operation)
        }
        refreshOperationHistory()
    }

    /**
     * Erases all operations after selected position and applies the new operation
     */
    fun applyForced(operation: Operation)
    {
        if (!Settings.hideObservingOperations || operation !is IObservingOperation)
            operationList.add(operation)
        operationList.subList(currentPosition, operationList.size).clear()
        operationList.add(operation)
        currentPosition = operationList.size - 1
        val chunks = operation.alteredChunks
        if (chunks != null)
            redrawChunkArea(chunks)
    }

    val all: List<Operation>
        get() = ArrayList(operationList)

    fun getOperation(i: Int): Operation
    {
        return operationList[i]
    }

    fun moveBack()
    {
        setPosition(currentPosition--)
    }

    fun moveForward()
    {
        setPosition(currentPosition++)
    }

    fun getPosition(): Int
    {
        return currentPosition
    }

    fun setPosition(newPosition: Int)
    {
        var currentPosition = newPosition
        val alteredChunks = mutableListOf<ChunkArea>()
        currentPosition = max(0, min(currentPosition, operationList.size - 1))
        if (currentPosition < this.currentPosition)
        {
            for (i in this.currentPosition downTo currentPosition + 1)
            {
                val operation = operationList[i]
                operation.rollback()
                val chunks = operation.alteredChunks
                if (chunks != null)
                    alteredChunks.add(chunks)
            }
            operationList[currentPosition].reapply()
        }
        else
        {
            for (i in this.currentPosition + 1..currentPosition)
            {
                val operation = operationList[i]
                operation.reapply()
                val chunks = operation.alteredChunks
                if (chunks != null)
                    alteredChunks.add(chunks)
            }
        }
        alteredChunks.forEach { redrawChunkArea(it) }
        this.currentPosition = currentPosition
    }

    private fun redrawChunkArea(chunkArea: ChunkArea)
    {
        ChunkArea(chunkArea.world, chunkArea.min.add(-1, -1), chunkArea.max.add(1, 1)).iterator().asSequence()
                .mapNotNull { chunkLoc -> editorTab.world.getChunk(chunkLoc) }
                .forEach { it.vertexData.invalidate() }
    }

    fun getAllAlteredChunks() : List<ChunkLocation>
    {
        //todo
        return operationList.mapNotNull { it.alteredChunks }.flatten()
    }

    fun getAllTrulyAlteredChunks() : List<ChunkLocation>
    {
        //todo
        return operationList.filter { it !is IObservingOperation }.mapNotNull { it.alteredChunks }.flatten()
    }
}
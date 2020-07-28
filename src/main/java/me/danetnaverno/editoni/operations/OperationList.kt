package me.danetnaverno.editoni.operations

import me.danetnaverno.editoni.editor.EditorGUI.refreshOperationHistory
import me.danetnaverno.editoni.editor.EditorTab
import me.danetnaverno.editoni.util.Translation.translate
import me.danetnaverno.editoni.world.Chunk
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*
import java.util.function.Consumer
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
            operationList.add(operation)
            currentPosition = operationList.size - 1
            operation.apply()
            operation.alteredChunks.forEach(Consumer { obj: Chunk -> obj.invalidateVertexes() }) //todo doesn't refresh _all_ potentially altered chunks
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
     * Erases all operations after selected position and applies the operation
     */
    fun applyForced(operation: Operation)
    {
        operationList.subList(currentPosition, operationList.size).clear()
        operationList.add(operation)
        currentPosition = operationList.size - 1
        operation.alteredChunks.forEach(Consumer { obj: Chunk -> obj.invalidateVertexes() }) //todo doesn't refresh _all_ potentially altered chunks
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
        val alteredChunks = mutableListOf<Chunk>()
        currentPosition = max(0, min(currentPosition, operationList.size - 1))
        if (currentPosition < this.currentPosition)
        {
            for (i in this.currentPosition downTo currentPosition + 1)
            {
                val operation = operationList[i]
                operation.rollback()
                alteredChunks.addAll(operation.alteredChunks)
            }
            operationList[currentPosition].apply()
        }
        else
        {
            for (i in this.currentPosition + 1..currentPosition)
            {
                val operation = operationList[i]
                operation.apply()
                alteredChunks.addAll(operation.alteredChunks)
            }
        }
        alteredChunks.forEach { it.invalidateVertexes() } //todo doesn't refresh _all_ potentially altered chunks
        this.currentPosition = currentPosition
    }
}
package me.danetnaverno.editoni.render

import me.danetnaverno.editoni.location.BlockLocationMutable
import me.danetnaverno.editoni.world.Chunk
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.BufferOverflowException
import java.nio.FloatBuffer

class ChunkRenderer(private val chunk: Chunk)
{
    val isBuilt: Boolean
        get() = vertexCount != -1

    private var vao = 0
    private var vbo = 0
    private var vertexCount = -1

    fun invalidate()
    {
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vbo)
        vertexCount = -1
    }

    fun updateVertices()
    {
        vertexBuffer.position(0)
        vertexBuffer.limit(32768)
        val mutableLocation = BlockLocationMutable(0, 0, 0)

        try
        {
            for (section in 0..15)
            {
                val blockTypes = chunk.blockTypes[section] ?: continue
                for (index in 0..4095)
                {
                    val blockType = blockTypes[index] ?: continue
                    mutableLocation.blockLocationFromSectionIndex(chunk, section, index)
                    val renderer = blockType.renderer
                    if (renderer.isVisible(chunk.world, mutableLocation))
                    {
                        try
                        {
                            if (vertexBuffer.position() + renderer.getMaxVertexCount() * 6 >= vertexBuffer.limit())
                                vertexBuffer.limit(vertexBuffer.limit() + 32768)
                            renderer.bake(chunk.world, mutableLocation, vertexBuffer)
                        }
                        catch (e: Exception)
                        {
                            throw e
                        }
                    }
                }
            }

            vertexCount = vertexBuffer.position() / 2

            if (vertexCount == 0)
                return

            vertexBuffer.flip()

            vao = glGenVertexArrays()
            glBindVertexArray(vao)

            vbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * 6, 0)
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 4 * 6, 4 * 3)
            glEnableVertexAttribArray(1)
        }
        catch (bufferException: BufferOverflowException)
        {
            //todo consider heuristics to shrink the buffer back if it gets too big but only for a rare occasion
            vertexBuffer = growBuffer(vertexBuffer)
        }
        finally
        {
        }
    }

    fun draw()
    {
        if (vertexCount <= 0)
            return

        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLES, 0, vertexCount)
    }

    private fun growBuffer(vertexBuffer: FloatBuffer): FloatBuffer
    {
        try
        {
            val newCapacity = vertexBuffer.capacity() + 1048576
            val newVertexBuffer = MemoryUtil.memAllocFloat(newCapacity)

            val vertexBufferPos = vertexBuffer.position()
            vertexBuffer.flip()
            MemoryUtil.memCopy(vertexBuffer, newVertexBuffer)
            newVertexBuffer.position(vertexBufferPos)

            return newVertexBuffer
        }
        finally
        {
            MemoryUtil.memFree(vertexBuffer)
        }
    }

    companion object
    {
        var vertexBuffer: FloatBuffer = MemoryUtil.memAllocFloat(1048576)
    }
}
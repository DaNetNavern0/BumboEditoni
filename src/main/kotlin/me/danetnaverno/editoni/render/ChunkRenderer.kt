package me.danetnaverno.editoni.render

import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.world.Chunk
import org.lwjgl.opengl.GL44.*
import org.lwjgl.system.MemoryUtil
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
        var vertexBuffer = MemoryUtil.memAllocFloat(32768)
        val mutableLocation = BlockLocation.Mutable(0, 0, 0)

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
                        if (vertexBuffer.position() + renderer.getMaxVertexCount() * 6 >= vertexBuffer.capacity()) //todo
                            vertexBuffer = growBuffer(vertexBuffer)
                        renderer.bake(chunk.world, mutableLocation, vertexBuffer)
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
            glBufferStorage(GL_ARRAY_BUFFER, vertexBuffer, 0)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * 6, 0)
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 4 * 6, 4 * 3)
            glEnableVertexAttribArray(1)
        }
        finally
        {
            MemoryUtil.memFree(vertexBuffer)
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
        val newCapacity = vertexBuffer.capacity() + 32768
        val newVertexBuffer = MemoryUtil.memAllocFloat(newCapacity)

        val vertexBufferPos = vertexBuffer.position()
        vertexBuffer.flip()
        MemoryUtil.memCopy(vertexBuffer, newVertexBuffer)
        newVertexBuffer.position(vertexBufferPos)

        MemoryUtil.memFree(vertexBuffer)

        return newVertexBuffer
    }
}
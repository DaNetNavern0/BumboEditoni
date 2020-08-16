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
    private var vboVertexes = 0
    private var vboUV = 0
    private var vertexCount = -1

    fun invalidate()
    {
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vboVertexes)
        glDeleteBuffers(vboUV)
        vertexCount = -1
    }

    fun updateVertexes()
    {
        var vertexBuffer = MemoryUtil.memAllocFloat(32768)
        var uvBuffer = MemoryUtil.memAllocFloat(32768)
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
                    if (blockType.renderer.isVisible(chunk.world, mutableLocation))
                        blockType.renderer.bake(chunk.world, mutableLocation, vertexBuffer, uvBuffer)

                    if (vertexBuffer.position() >= vertexBuffer.capacity() - 100) //todo
                    {
                        val pair = growBuffers(vertexBuffer, uvBuffer)
                        vertexBuffer = pair.first
                        uvBuffer = pair.second
                    }
                }
            }

            vertexCount = vertexBuffer.position()

            if (vertexCount == 0)
                return

            vertexBuffer.flip()
            uvBuffer.flip()

            vao = glGenVertexArrays()
            glBindVertexArray(vao)

            vboVertexes = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vboVertexes)
            glBufferStorage(GL_ARRAY_BUFFER, vertexBuffer, 0)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
            glEnableVertexAttribArray(0)

            vboUV = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vboUV)
            glBufferStorage(GL_ARRAY_BUFFER, uvBuffer, 0)
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0)
            glEnableVertexAttribArray(1)
            glBindVertexArray(0)
        }
        finally
        {
            MemoryUtil.memFree(vertexBuffer)
            MemoryUtil.memFree(uvBuffer)
        }
    }

    fun draw()
    {
        if (vertexCount <= 0)
            return

        glBindVertexArray(vao)
        glDrawArrays(GL_QUADS, 0, vertexCount)
    }

    private fun growBuffers(vertexBuffer: FloatBuffer, uvBuffer: FloatBuffer): Pair<FloatBuffer, FloatBuffer>
    {
        val newCapacity = vertexBuffer.capacity() + 32768

        val newVertexBuffer = MemoryUtil.memAllocFloat(newCapacity)
        MemoryUtil.memCopy(vertexBuffer, newVertexBuffer)

        val newUvBuffer = MemoryUtil.memAllocFloat(newCapacity)
        MemoryUtil.memCopy(uvBuffer, newUvBuffer)

        MemoryUtil.memFree(vertexBuffer)
        MemoryUtil.memFree(uvBuffer)

        return Pair(newVertexBuffer, newUvBuffer)
    }
}
package me.danetnaverno.editoni.render

import kotlinx.coroutines.launch
import me.danetnaverno.editoni.location.BlockLocationMutable
import me.danetnaverno.editoni.util.MainThreadScope
import me.danetnaverno.editoni.world.Chunk
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

class ChunkRenderer(private val chunk: Chunk)
{
    var isBuilt = false
        private set

    private var vao = 0
    private var vbo = 0
    private var vertexCount = -1

    fun invalidate()
    {
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vbo)
        vertexCount = -1
        isBuilt = false
    }

    fun updateVertices()
    {
        var vertexBuffer = MemoryUtil.memAllocFloat(1048576)

        val mutableLocation = BlockLocationMutable(0, 0, 0)

        //todo I think this entire section isn't nice. Once my Chunk will store data in a palette way, I'll redo this.
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
                    val targetBufferSize = vertexBuffer.position() + renderer.getMaxVertexCount() * 6
                    if (targetBufferSize >= vertexBuffer.capacity())
                        vertexBuffer = growBuffer(vertexBuffer)
                    renderer.bake(chunk.world, mutableLocation, vertexBuffer)
                }
            }
        }

        isBuilt = true
        vertexCount = vertexBuffer.position() / 2
        if (vertexCount == 0)
            return

        vertexBuffer.flip()

        MainThreadScope.launch {
            vao = glGenVertexArrays()
            glBindVertexArray(vao)

            vbo = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vbo)
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * 6, 0)
            glEnableVertexAttribArray(0)
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 4 * 6, 4 * 3)
            glEnableVertexAttribArray(1)
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
}
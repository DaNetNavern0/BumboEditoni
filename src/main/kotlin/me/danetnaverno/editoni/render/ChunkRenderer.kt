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
    var buildState = State.Unbuilt
        private set

    private var vao = 0
    private var vbo = 0
    private var vertexCount = -1

    fun invalidate()
    {
        //Discard any attempt to invalidate a chunk that's in a process of being baked.
        //Such an attempt can potentially happen, because baking happens across multiple threads.
        if (buildState == State.Building)
            return
        glDeleteVertexArrays(vao)
        glDeleteBuffers(vbo)
        vao = 0
        vbo = 0
        vertexCount = -1
        buildState = State.Unbuilt
    }

    fun updateVertices()
    {
        buildState = State.Building
        var vertexBuffer = MemoryUtil.memAllocFloat(1048576)

        try
        {
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

            if (vertexBuffer.position() > 0)
                MainThreadScope.launch { submitBuffer(vertexBuffer) }
        }
        catch (e: Exception)
        {
            MemoryUtil.memFree(vertexBuffer)
            buildState = State.Error
            throw e
        }
    }

    internal fun draw()
    {
        if (vertexCount <= 0)
            return
        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLES, 0, vertexCount)
    }

    private fun submitBuffer(vertexBuffer: FloatBuffer)
    {
        try
        {
            //If we called updateVertices() without prior invalidate(), then vao will not equal 0 and we should invalidate those buffers
            if (vao != 0)
            {
                glDeleteVertexArrays(vao)
                glDeleteBuffers(vbo)
            }

            vertexCount = vertexBuffer.position() / 2
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

            buildState = State.Built
        }
        catch (e: Exception)
        {
            buildState = State.Error
            throw e
        }
        finally
        {
            MemoryUtil.memFree(vertexBuffer)
        }
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

    enum class State
    {
        Unbuilt, Building, Built, Error
    }
}
package me.danetnaverno.editoni.render

import me.danetnaverno.editoni.location.BlockArea
import me.danetnaverno.editoni.location.EntityLocation
import me.danetnaverno.editoni.texture.Texture
import me.danetnaverno.editoni.texture.TextureAtlas
import me.danetnaverno.editoni.util.ResourceLocation
import org.lwjgl.opengl.GL44.*
import org.lwjgl.system.MemoryUtil

class SelectionRenderer
{
    val isBuilt: Boolean
        get() = vaoBox != 0

    private var vaoLines: Int = 0
    private var vboLines: Int = 0

    private var vaoBox: Int = 0
    private var vboVertexes: Int = 0
    private var vboUV: Int = 0

    fun invalidate()
    {
        glDeleteVertexArrays(vaoLines)
        glDeleteVertexArrays(vaoBox)
        glDeleteBuffers(vboLines)
        glDeleteBuffers(vboVertexes)
        glDeleteBuffers(vboUV)
    }

    fun update(area: BlockArea)
    {
        invalidate()

        val tIndex = TextureAtlas.mainAtlas.getZLayer(Texture[ResourceLocation("common:select")])

        val min = EntityLocation(area.min.globalX - 0.01, area.min.globalY - 0.01, area.min.globalZ - 0.01)
        val max = EntityLocation(area.max.globalX + 1.01, area.max.globalY + 1.01, area.max.globalZ + 1.01)

        val minX = min.globalX.toFloat()
        val minY = min.globalY.toFloat()
        val minZ = min.globalZ.toFloat()

        val maxX = max.globalX.toFloat()
        val maxY = max.globalY.toFloat()
        val maxZ = max.globalZ.toFloat()

        val size = max.subtract(min)
        val uvX = size.globalX.toFloat()
        val uvZ = size.globalZ.toFloat()

        bakeLines(minX, minY, minZ, maxX, maxY, maxZ)

        vaoBox = glGenVertexArrays()
        glBindVertexArray(vaoBox)
        bakeVertices(minX, minY, minZ, maxX, maxY, maxZ)
        bakeUV(uvX, uvZ, tIndex)
    }

    private fun bakeLines(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float)
    {
        val lineBuffer = MemoryUtil.memAllocFloat(12 * 6)

        vaoLines = glGenVertexArrays()
        glBindVertexArray(vaoLines)

        lineBuffer.put(floatArrayOf (
                minX, minY, minZ,
                maxX, minY, minZ,
                maxX, minY, minZ,
                maxX, maxY, minZ,
                maxX, maxY, minZ,
                minX, maxY, minZ,
                minX, maxY, minZ,
                minX, minY, minZ,

                minX, minY, maxZ,
                maxX, minY, maxZ,
                maxX, minY, maxZ,
                maxX, maxY, maxZ,
                maxX, maxY, maxZ,
                minX, maxY, maxZ,
                minX, maxY, maxZ,
                minX, minY, maxZ,

                minX, minY, minZ,
                minX, minY, maxZ,
                minX, maxY, minZ,
                minX, maxY, maxZ,
                maxX, maxY, minZ,
                maxX, maxY, maxZ,
                maxX, minY, minZ,
                maxX, minY, maxZ,
        ))
        lineBuffer.flip()

        vboLines = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboLines)
        glBufferStorage(GL_ARRAY_BUFFER, lineBuffer, 0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(0)

        MemoryUtil.memFree(lineBuffer)
    }


    private fun bakeVertices(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float)
    {
        val vertexBuffer = MemoryUtil.memAllocFloat(6 * 4 * 3)

        vertexBuffer.put(maxX).put(minY).put(minZ)
        vertexBuffer.put(maxX).put(minY).put(maxZ)
        vertexBuffer.put(minX).put(minY).put(maxZ)
        vertexBuffer.put(minX).put(minY).put(minZ)

        vertexBuffer.put(minX).put(maxY).put(minZ)
        vertexBuffer.put(minX).put(maxY).put(maxZ)
        vertexBuffer.put(maxX).put(maxY).put(maxZ)
        vertexBuffer.put(maxX).put(maxY).put(minZ)

        vertexBuffer.put(minX).put(minY).put(minZ)
        vertexBuffer.put(minX).put(minY).put(maxZ)
        vertexBuffer.put(minX).put(maxY).put(maxZ)
        vertexBuffer.put(minX).put(maxY).put(minZ)


        vertexBuffer.put(minX).put(maxY).put(minZ)
        vertexBuffer.put(maxX).put(maxY).put(minZ)
        vertexBuffer.put(maxX).put(minY).put(minZ)
        vertexBuffer.put(minX).put(minY).put(minZ)


        vertexBuffer.put(minX).put(minY).put(maxZ)
        vertexBuffer.put(maxX).put(minY).put(maxZ)
        vertexBuffer.put(maxX).put(maxY).put(maxZ)
        vertexBuffer.put(minX).put(maxY).put(maxZ)


        vertexBuffer.put(maxX).put(maxY).put(minZ)
        vertexBuffer.put(maxX).put(maxY).put(maxZ)
        vertexBuffer.put(maxX).put(minY).put(maxZ)
        vertexBuffer.put(maxX).put(minY).put(minZ)

        vertexBuffer.flip()

        vboVertexes = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexes)
        glBufferStorage(GL_ARRAY_BUFFER, vertexBuffer, 0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(0)

        MemoryUtil.memFree(vertexBuffer)
    }


    private fun bakeUV(uvX: Float, uvZ: Float, tIndex: Float)
    {
        val uvBuffer = MemoryUtil.memAllocFloat(6 * 4 * 3)

        uvBuffer.put(uvX).put(0.0f).put(tIndex)
        uvBuffer.put(uvX).put(uvZ).put(tIndex)
        uvBuffer.put(0.0f).put(uvZ).put(tIndex)
        uvBuffer.put(0.0f).put(uvZ).put(tIndex)


        uvBuffer.put(0.0f).put(0.0f).put(tIndex)
        uvBuffer.put(0.0f).put(uvZ).put(tIndex)
        uvBuffer.put(uvX).put(uvZ).put(tIndex)
        uvBuffer.put(uvX).put(0.0f).put(tIndex)

        uvBuffer.put(0.0f).put(0.0f).put(tIndex)
        uvBuffer.put(0.0f).put(uvZ).put(tIndex)
        uvBuffer.put(uvX).put(uvZ).put(tIndex)
        uvBuffer.put(uvX).put(0.0f).put(tIndex)

        uvBuffer.put(0.0f).put(uvZ).put(tIndex)
        uvBuffer.put(uvX).put(uvZ).put(tIndex)
        uvBuffer.put(uvX).put(0.0f).put(tIndex)
        uvBuffer.put(0.0f).put(0.0f).put(tIndex)


        uvBuffer.put(0.0f).put(0.0f).put(tIndex)
        uvBuffer.put(uvX).put(0.0f).put(tIndex)
        uvBuffer.put(uvX).put(uvZ).put(tIndex)
        uvBuffer.put(0.0f).put(uvZ).put(tIndex)


        uvBuffer.put(uvX).put(0.0f).put(tIndex)
        uvBuffer.put(uvX).put(uvZ).put(tIndex)
        uvBuffer.put(0.0f).put(uvZ).put(tIndex)
        uvBuffer.put(0.0f).put(0.0f).put(tIndex)

        uvBuffer.flip()

        vboUV = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboUV)
        glBufferStorage(GL_ARRAY_BUFFER, uvBuffer, 0)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(1)
        glBindVertexArray(0)

        MemoryUtil.memFree(uvBuffer)
    }

    fun draw()
    {
        if (vaoBox == 0)
            return

        glLineWidth(2f)
        glDisable(GL_DEPTH_TEST)
        glBindVertexArray(vaoLines)
        glDrawArrays(GL_LINES, 0, 12 * 3)
        glEnable(GL_DEPTH_TEST)

        glBindVertexArray(vaoBox)
        glDrawArrays(GL_QUADS, 0, 6 * 4 * 3)
    }
}
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
    private var vboVertices: Int = 0

    fun invalidate()
    {
        glDeleteVertexArrays(vaoLines)
        glDeleteVertexArrays(vaoBox)
        glDeleteBuffers(vboLines)
        glDeleteBuffers(vboVertices)
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
        val uvY = size.globalY.toFloat()
        val uvZ = size.globalZ.toFloat()

        bakeLines(minX, minY, minZ, maxX, maxY, maxZ)

        vaoBox = glGenVertexArrays()
        glBindVertexArray(vaoBox)
        bakeVertices(minX, minY, minZ, maxX, maxY, maxZ, uvX, uvY, uvZ, tIndex)
    }

    private fun bakeLines(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float)
    {
        val lineBuffer = MemoryUtil.memAllocFloat(12 * 6)

        vaoLines = glGenVertexArrays()
        glBindVertexArray(vaoLines)

        //I don't like how it looks, but it seems to be the most optimized way of doing this.
        //Method put(<float_array>) has individual put(float) calls under the hood anyway.
        lineBuffer.put(minX).put(minY).put(minZ)
        lineBuffer.put(minX).put(minY).put(maxZ)

        lineBuffer.put(minX).put(minY).put(minZ)
        lineBuffer.put(minX).put(maxY).put(minZ)

        lineBuffer.put(minX).put(minY).put(minZ)
        lineBuffer.put(maxX).put(minY).put(minZ)

        lineBuffer.put(maxX).put(maxY).put(maxZ)
        lineBuffer.put(maxX).put(maxY).put(minZ)

        lineBuffer.put(maxX).put(maxY).put(maxZ)
        lineBuffer.put(maxX).put(minY).put(maxZ)

        lineBuffer.put(maxX).put(maxY).put(maxZ)
        lineBuffer.put(minX).put(maxY).put(maxZ)


        lineBuffer.put(minX).put(maxY).put(minZ)
        lineBuffer.put(maxX).put(maxY).put(minZ)

        lineBuffer.put(minX).put(minY).put(minZ)
        lineBuffer.put(minX).put(minY).put(minZ)

        lineBuffer.put(minX).put(minY).put(minZ)
        lineBuffer.put(minX).put(minY).put(minZ)

        lineBuffer.put(minX).put(minY).put(minZ)
        lineBuffer.put(minX).put(minY).put(minZ)

        lineBuffer.put(minX).put(minY).put(minZ)
        lineBuffer.put(minX).put(minY).put(minZ)

        lineBuffer.put(minX).put(minY).put(minZ)
        lineBuffer.put(minX).put(minY).put(minZ)

        lineBuffer.flip()

        vboLines = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboLines)
        glBufferStorage(GL_ARRAY_BUFFER, lineBuffer, 0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(0)

        MemoryUtil.memFree(lineBuffer)
    }


    private fun bakeVertices(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float, uvX: Float, uvY: Float, uvZ: Float, tIndex: Float)
    {
        val vertexBuffer = MemoryUtil.memAllocFloat(6 * 6 * 6)

        //I don't like how it looks, but it seems to be the most optimized way of doing this.
        //Method put(<float_array>) has individual put(float) calls under the hood anyway.
        vertexBuffer
                .put(minX).put(maxY).put(minZ).put(0f).put(0f).put(tIndex)
                .put(minX).put(maxY).put(maxZ).put(0f).put(uvZ).put(tIndex)
                .put(maxX).put(maxY).put(maxZ).put(uvX).put(uvZ).put(tIndex)

                .put(maxX).put(maxY).put(maxZ).put(uvX).put(uvZ).put(tIndex)
                .put(maxX).put(maxY).put(minZ).put(uvX).put(0.0f).put(tIndex)
                .put(minX).put(maxY).put(minZ).put(0f).put(0f).put(tIndex)


                .put(maxX).put(minY).put(maxZ).put(0f).put(0f).put(tIndex)
                .put(minX).put(minY).put(maxZ).put(0f).put(uvZ).put(tIndex)
                .put(minX).put(minY).put(minZ).put(uvX).put(uvZ).put(tIndex)

                .put(minX).put(minY).put(minZ).put(uvX).put(uvZ).put(tIndex)
                .put(maxX).put(minY).put(minZ).put(uvX).put(0.0f).put(tIndex)
                .put(maxX).put(minY).put(maxZ).put(0f).put(0f).put(tIndex)


                .put(maxX).put(maxY).put(maxZ).put(uvX).put(uvY).put(tIndex)
                .put(minX).put(maxY).put(maxZ).put(0f).put(uvY).put(tIndex)
                .put(minX).put(minY).put(maxZ).put(0f).put(0f).put(tIndex)

                .put(minX).put(minY).put(maxZ).put(0f).put(0f).put(tIndex)
                .put(maxX).put(minY).put(maxZ).put(uvX).put(0f).put(tIndex)
                .put(maxX).put(maxY).put(maxZ).put(uvX).put(uvY).put(tIndex)


                .put(maxX).put(minY).put(minZ).put(uvX).put(0f).put(tIndex)
                .put(minX).put(minY).put(minZ).put(0f).put(0f).put(tIndex)
                .put(minX).put(maxY).put(minZ).put(0f).put(uvY).put(tIndex)

                .put(minX).put(maxY).put(minZ).put(0f).put(uvY).put(tIndex)
                .put(maxX).put(maxY).put(minZ).put(uvX).put(uvY).put(tIndex)
                .put(maxX).put(minY).put(minZ).put(uvX).put(0f).put(tIndex)


                .put(minX).put(maxY).put(maxZ).put(uvY).put(uvZ).put(tIndex)
                .put(minX).put(maxY).put(minZ).put(uvY).put(0f).put(tIndex)
                .put(minX).put(minY).put(minZ).put(0f).put(0f).put(tIndex)

                .put(minX).put(minY).put(minZ).put(0f).put(0f).put(tIndex)
                .put(minX).put(minY).put(maxZ).put(0f).put(uvZ).put(tIndex)
                .put(minX).put(maxY).put(maxZ).put(uvY).put(uvZ).put(tIndex)


                .put(maxX).put(maxY).put(minZ).put(uvY).put(0f).put(tIndex)
                .put(maxX).put(maxY).put(maxZ).put(uvY).put(uvZ).put(tIndex)
                .put(maxX).put(minY).put(maxZ).put(0f).put(uvZ).put(tIndex)

                .put(maxX).put(minY).put(maxZ).put(0f).put(uvZ).put(tIndex)
                .put(maxX).put(minY).put(minZ).put(0f).put(0f).put(tIndex)
                .put(maxX).put(maxY).put(minZ).put(uvY).put(0f).put(tIndex)

        vertexBuffer.flip()

        vboVertices = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboVertices)
        glBufferStorage(GL_ARRAY_BUFFER, vertexBuffer, 0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * 6, 0)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 4 * 6, 4 * 3)
        glEnableVertexAttribArray(1)

        MemoryUtil.memFree(vertexBuffer)
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
        glDrawArrays(GL_TRIANGLES, 0, 6 * 4 * 3)
    }
}
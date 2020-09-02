package me.danetnaverno.editoni.render

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.editor.EditorTab
import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.location.ChunkLocationMutable
import me.danetnaverno.editoni.world.ChunkTicketCamera
import me.danetnaverno.editoni.world.ChunkManager
import org.lwjgl.opengl.GL44.glBindVertexArray
import kotlin.math.abs

class WorldRenderer(private val tab: EditorTab)
{
    fun bake()
    {
        val renderDistance = Editor.renderDistance
        val cameraLocation = BlockLocation(tab.camera.x.toInt(), tab.camera.y.toInt(), tab.camera.z.toInt()).toChunkLocation()
        val chunkLocation = ChunkLocationMutable(cameraLocation.x - renderDistance, cameraLocation.z - renderDistance)

        for (x in 0 until renderDistance * 2)
        {
            for (z in 0 until renderDistance * 2)
                tab.world.loadChunkAsync(chunkLocation.add(1, 0), ChunkTicketCamera)
            chunkLocation.add(-renderDistance * 2, 1)
        }

        for (chunk in tab.world.getLoadedChunks())
        {
            if (abs(chunk.location.x - cameraLocation.x) <= renderDistance && abs(chunk.location.z - cameraLocation.z) <= renderDistance)
                ChunkManager.addTicket(chunk, ChunkTicketCamera)
            else
                ChunkManager.removeTicket(chunk, ChunkTicketCamera)
        }

        for (chunk in tab.world.getLoadedChunks())
            if (!chunk.vertexData.isBuilt)
                chunk.vertexData.updateVertices()
    }

    fun render()
    {
        for (chunk in tab.world.getLoadedChunks())
            chunk.draw()
        glBindVertexArray(0)
    }
}
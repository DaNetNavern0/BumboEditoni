package me.danetnaverno.editoni.render

import me.danetnaverno.editoni.editor.EditorTab
import me.danetnaverno.editoni.editor.Settings
import me.danetnaverno.editoni.location.ChunkLocationMutable
import me.danetnaverno.editoni.world.ChunkTicketCamera
import org.lwjgl.opengl.GL33.glBindVertexArray

class WorldRenderer(private val tab: EditorTab)
{
    fun bake()
    {
        val renderDistance = Settings.renderDistance
        val cameraLocation = tab.camera.mutableLocation.toChunkLocation()
        val chunkLocation = ChunkLocationMutable(cameraLocation.x - renderDistance, cameraLocation.z - renderDistance)

        for (x in 0 until renderDistance * 2)
        {
            for (z in 0 until renderDistance * 2)
                tab.world.loadChunkAsync(chunkLocation.add(1, 0), ChunkTicketCamera)
            chunkLocation.add(-renderDistance * 2, 1)
        }

        for (chunk in tab.world.getLoadedChunks())
            if (!chunk.vertexData.isBuilt)
                chunk.vertexData.updateVertices()
    }

    fun render()
    {
        for (chunk in tab.world.getLoadedChunks())
            chunk.vertexData.draw()
        glBindVertexArray(0)
    }
}
package me.danetnaverno.editoni.render

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.editor.EditorTab
import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.util.RobertoGarbagio
import me.danetnaverno.editoni.world.ChunkTicketCamera
import me.danetnaverno.editoni.world.ChunkTicketManager
import me.danetnaverno.editoni.world.World
import org.lwjgl.opengl.GL44.glBindVertexArray

class WorldRenderer(private val tab: EditorTab)
{
    fun bake()
    {
        val renderDistance = Editor.renderDistance
        val cameraLocation = BlockLocation(tab.camera.x.toInt(), tab.camera.y.toInt(), tab.camera.z.toInt()).toChunkLocation()
        val chunkLocation = ChunkLocation.Mutable(cameraLocation.x - renderDistance, cameraLocation.z - renderDistance)

        val cameraTicket = ChunkTicketCamera()
        for (x in 0 until renderDistance * 2)
        {
            for (z in 0 until renderDistance * 2)
                tab.world.loadChunkAt(chunkLocation.addMutably(1, 0), cameraTicket)
            tab.world.loadChunkAt(chunkLocation.addMutably(-renderDistance * 2, 1), cameraTicket)
        }

        ChunkTicketManager.cleanCameraTickets(tab.world)
        for (chunk in tab.world.getLoadedChunks().filter { it.location.distance(cameraLocation) <= renderDistance })
            ChunkTicketManager.addTicket(chunk, cameraTicket)

        for (chunk in tab.world.getLoadedChunks())
            if (!chunk.vertexData.isBuilt)
                chunk.vertexData.updateVertexes()
    }

    fun render()
    {
        for (chunk in tab.world.getLoadedChunks())
            chunk.draw()
        glBindVertexArray(0)
    }
}
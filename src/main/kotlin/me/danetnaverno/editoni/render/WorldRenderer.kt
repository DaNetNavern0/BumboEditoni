package me.danetnaverno.editoni.render

import me.danetnaverno.editoni.editor.Settings
import me.danetnaverno.editoni.location.IChunkLocation
import me.danetnaverno.editoni.world.Chunk
import me.danetnaverno.editoni.world.World
import org.lwjgl.opengl.GL33.glBindVertexArray
import java.util.concurrent.ConcurrentLinkedQueue

class WorldRenderer(private val world: World)
{
    private val chunksToBake = ConcurrentLinkedQueue<Chunk>()

    //todo multi-threaded/off-threaded baking would be really nice
    fun tickBaking(cameraChunkLocation: IChunkLocation)
    {
        //Here we're iterating over loaded, but unrendered chunks and trying to render them
        //However, we leave chunks which are loaded, but outside of the render distance not baked.
        //For more info see [Settings.chunkLoadDistance]
        chunksToBake.removeIf { chunk ->
            val renderDistance = Settings.renderDistance
            if (!chunk.vertexData.isBuilt && cameraChunkLocation.withinCubicDistance(chunk.chunkLocation, renderDistance))
            {
                chunk.vertexData.updateVertices()
                return@removeIf true
            }
            false
        }
    }

    fun render()
    {
        for (chunk in world.getLoadedChunks())
            chunk.vertexData.draw()
        glBindVertexArray(0)
    }

    fun bakeChunk(chunk: Chunk)
    {
        chunk.vertexData.invalidate()
        chunksToBake.add(chunk)
    }
}
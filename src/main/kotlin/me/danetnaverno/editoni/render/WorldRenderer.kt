package me.danetnaverno.editoni.render

import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.danetnaverno.editoni.editor.Settings
import me.danetnaverno.editoni.location.IChunkLocation
import me.danetnaverno.editoni.util.ChunkBakingScope
import me.danetnaverno.editoni.world.Chunk
import me.danetnaverno.editoni.world.World
import org.lwjgl.opengl.GL33.glBindVertexArray
import java.util.*

class WorldRenderer(private val world: World)
{
    /**
     * When a chunk gets loaded, it end up in this collection.
     *
     * If its within [Settings.renderDistance], it will be baked and ejected from a collection.
     *
     * If a chunk is between [Settings.renderDistance] and [Settings.chunkLoadDistance],
     *   it stay in this collection until they get within the render distance.
     */
    private val unbakedChunks = LinkedList<Chunk>()

    /**
     * Blocks the Main Thread and concurrently bakes all chunks within [Settings.renderDistance], which aren't built yet.
     *
     * Because the Main Thread will be blocked, and only the Main Thread can change the world,
     *   we're safe to read the world (while baking chunks) in an unsync manner
     */
    internal fun tickBaking(cameraChunkLocation: IChunkLocation)
    {
        runBlocking(ChunkBakingScope.coroutineContext) {
            val tasks = arrayListOf<Job>()
            val iterator = unbakedChunks.iterator()
            val renderDistance = Settings.renderDistance
            while(iterator.hasNext())
            {
                val chunk = iterator.next()
                if (cameraChunkLocation.withinCubicDistance(chunk.chunkLocation, renderDistance) && !chunk.renderer.isBuilt)
                {
                    tasks.add(ChunkBakingScope.launch {
                        chunk.renderer.updateVertices()
                    })
                    iterator.remove()
                }
            }
            tasks.joinAll()
        }
    }

    internal fun render()
    {
        for (chunk in world.getLoadedChunks())
            chunk.renderer.draw()
        glBindVertexArray(0)
    }

    internal fun markChunkToBake(chunk: Chunk)
    {
        chunk.renderer.invalidate()
        unbakedChunks.add(chunk)
    }
}
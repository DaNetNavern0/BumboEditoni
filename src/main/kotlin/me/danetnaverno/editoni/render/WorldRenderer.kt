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
     * Concurrently bakes all chunks within [Settings.renderDistance], which aren't built yet.
     *
     * Because it blocks the Main Thread until the job is done, and only the Main Thread can change the world,
     *   we're safe to read the world while baking chunks in an unsync manner.
     *
     * Note: the process of binding bakes buffers is done in the Main Thread once they're all done.
     */
    internal fun tickBaking(cameraChunkLocation: IChunkLocation)
    {
        val tasks = arrayListOf<Job>()
        val iterator = unbakedChunks.iterator()
        val renderDistance = Settings.renderDistance
        while (iterator.hasNext())
        {
            val chunk = iterator.next()
            if (cameraChunkLocation.withinCubicDistance(chunk.chunkLocation, renderDistance) && chunk.renderer.buildState === ChunkRenderer.State.Unbuilt)
            {
                tasks.add(ChunkBakingScope.launch {
                    chunk.renderer.updateVertices()
                })
                iterator.remove()
            }
        }
        if (tasks.isNotEmpty())
            runBlocking(ChunkBakingScope.coroutineContext) {
                tasks.joinAll()
            }
    }

    internal fun draw()
    {
        for (chunk in world.getLoadedChunks())
            chunk.renderer.draw()
    }

    internal fun markChunkToBake(chunk: Chunk)
    {
        chunk.renderer.invalidate()
        unbakedChunks.add(chunk)
    }
}
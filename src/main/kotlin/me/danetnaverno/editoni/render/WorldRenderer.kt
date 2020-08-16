package me.danetnaverno.editoni.render

import me.danetnaverno.editoni.editor.Editor
import me.danetnaverno.editoni.editor.EditorTab
import me.danetnaverno.editoni.location.BlockLocation
import org.lwjgl.opengl.GL44.*
import java.util.stream.Collectors
import kotlin.math.abs

class WorldRenderer(private val tab: EditorTab)
{
    fun bake()
    {
        val renderDistance = Editor.renderDistance
        val cameraLocation = BlockLocation(tab.camera.x.toInt(), tab.camera.y.toInt(), tab.camera.z.toInt()).toChunkLocation()
        val visibleRegions = tab.world.getRegions().stream().filter { it.location.distance(cameraLocation.toRegionLocation()) <= 2 }.collect(Collectors.toList())
        for (region in visibleRegions)
        {
            for (x in -renderDistance..renderDistance) for (z in -renderDistance..renderDistance)
            {
                val chunkCamLoc = cameraLocation.add(x, z)
                if (chunkCamLoc.toRegionLocation() == region.location)
                    region.loadChunkAt(chunkCamLoc)
            }
            val visibleChunks = region.getLoadedChunks()
                    .filter { abs(it.location.x - cameraLocation.x) <= renderDistance || abs(it.location.z - cameraLocation.z) <= renderDistance }
            for (chunk in visibleChunks)
            {
                glBegin(GL_QUADS)
                glVertex3i(chunk.location.x * 16, 0, chunk.location.z * 16)
                glTexCoord2f(0.0f, 0.0f)
                glVertex3i(chunk.location.x * 16, 0, (chunk.location.z + 1) * 16)
                glTexCoord2f(0.0f, 1.0f)
                glVertex3i((chunk.location.x + 1) * 16, 0, (chunk.location.z + 1) * 16)
                glTexCoord2f(1.0f, 1.0f)
                glVertex3i((chunk.location.x + 1) * 16, 0, chunk.location.z * 16)
                glTexCoord2f(1.0f, 0.0f)
                glEnd()
            }
            for (chunk in visibleChunks)
            {
                if (!chunk.vertexData.isBuilt)
                    chunk.vertexData.updateVertexes()

                //for (Entity entity : chunk.getEntities())
                //    entity.getType().getRenderer().draw(entity);
            }
        }
    }

    fun render()
    {
        val renderDistance = Editor.renderDistance
        val cameraLocation = BlockLocation(tab.camera.x.toInt(), tab.camera.y.toInt(), tab.camera.z.toInt()).toChunkLocation()
        val visibleRegions = tab.world.getRegions().stream().filter { it.location.distance(cameraLocation.toRegionLocation()) <= 2 }.collect(Collectors.toList())
        for (region in visibleRegions)
        {
            val visibleChunks = region.getLoadedChunks()
                    .filter { abs(it.location.x - cameraLocation.x) <= renderDistance || abs(it.location.z - cameraLocation.z) <= renderDistance }
            for (chunk in visibleChunks)
            {
                chunk.draw()

                //for (Entity entity : chunk.getEntities())
                //    entity.getType().getRenderer().draw(entity);
            }
            glBindVertexArray(0)
        }
    }
}
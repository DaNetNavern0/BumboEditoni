package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.editor.Editor.renderDistance
import me.danetnaverno.editoni.editor.EditorTab
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.toRegionLocation
import org.lwjgl.opengl.GL11
import java.util.stream.Collectors
import kotlin.math.abs

class WorldRenderer(private val tab: EditorTab)
{
    fun render()
    {
        val cameraLocation = BlockLocation(tab.camera.x.toInt(), tab.camera.y.toInt(), tab.camera.z.toInt()).toChunkLocation()
        val visibleRegions = tab.world.getRegions().stream().filter { it.location.distance(cameraLocation.toRegionLocation()) <= 2 }.collect(Collectors.toList())
        for (region in visibleRegions)
        {
            val renderDistance = renderDistance
            for (x in -renderDistance..renderDistance) for (z in -renderDistance..renderDistance)
            {
                if (cameraLocation.add(x, z).toRegionLocation() == region.location)
                    region.loadChunkAt(cameraLocation.add(x, z))
            }
            val visibleChunks = region.getLoadedChunks()
                    .filter { abs(it.location.x - cameraLocation.x) <= renderDistance || abs(it.location.z - cameraLocation.z) <= renderDistance }
            for (chunk in visibleChunks)
            {
                //if ((chunk.location.x + chunk.location.z) % 2 == 0) get(ResourceLocation("common", "chunk_a")).bind() else get(ResourceLocation("common", "chunk_b")).bind()
                GL11.glBegin(GL11.GL_QUADS)
                GL11.glVertex3i(chunk.location.x * 16, 0, chunk.location.z * 16)
                GL11.glVertex3i(chunk.location.x * 16, 0, (chunk.location.z + 1) * 16)
                GL11.glVertex3i((chunk.location.x + 1) * 16, 0, (chunk.location.z + 1) * 16)
                GL11.glVertex3i((chunk.location.x + 1) * 16, 0, chunk.location.z * 16)
                GL11.glEnd()

                if (chunk.vertexCount == 0)
                    chunk.updateVertexes()

                chunk.draw()

                //for (Entity entity : chunk.getEntities())
                //    entity.getType().getRenderer().draw(entity);
            }
        }
    }
}
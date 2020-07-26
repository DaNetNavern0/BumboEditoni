package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.editor.Editor.renderDistance
import me.danetnaverno.editoni.texture.Texture.Companion.get
import me.danetnaverno.editoni.util.Camera
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.blockLocationFromSectionIndex
import me.danetnaverno.editoni.util.location.toRegionLocation
import org.lwjgl.opengl.GL11
import java.util.*
import java.util.stream.Collectors

class WorldRenderer(private val world: World)
{
    private val renderCache: MutableMap<Chunk, Array<BooleanArray?>> = HashMap()

    fun render()
    {
        val cameraLocation = BlockLocation(Camera.x.toInt(), Camera.y.toInt(), Camera.z.toInt()).toChunkLocation()
        val visibleRegions = world.getRegions().stream().filter { it: Region -> it.location.distance(cameraLocation.toRegionLocation()) <= 2 }.collect(Collectors.toList())
        for (region in visibleRegions)
        {
            val renderDistance = renderDistance
            for (x in -renderDistance..renderDistance) for (z in -renderDistance..renderDistance)
            {
                if (cameraLocation.add(x, z).toRegionLocation() == region.location) region.loadChunkAt(cameraLocation.add(x, z))
            }
            val visibleChunks = region.getLoadedChunks().stream()
                    .filter { it: Chunk ->
                        (Math.abs(it.location.x - cameraLocation.x) <= renderDistance
                                || Math.abs(it.location.z - cameraLocation.z) <= renderDistance)
                    }
                    .collect(Collectors.toList())
            for (chunk in visibleChunks)
            {
                if ((chunk.location.x + chunk.location.z) % 2 == 0) get(ResourceLocation("common", "chunk_a")).bind() else get(ResourceLocation("common", "chunk_b")).bind()
                GL11.glBegin(GL11.GL_QUADS)
                GL11.glVertex3i(chunk.location.x * 16, 0, chunk.location.z * 16)
                GL11.glVertex3i(chunk.location.x * 16, 0, (chunk.location.z + 1) * 16)
                GL11.glVertex3i((chunk.location.x + 1) * 16, 0, (chunk.location.z + 1) * 16)
                GL11.glVertex3i((chunk.location.x + 1) * 16, 0, chunk.location.z * 16)
                GL11.glEnd()
                val sections = chunk.blockTypes;

                for (section in 0..15)
                {
                    val blockTypes = sections[section] ?: continue
                    for (index in 0..15)
                    {
                        val blockType = blockTypes[index]
                        if (blockType == null || blockType.isHidden) continue
                        val location = blockLocationFromSectionIndex(chunk, section, index)
                        blockType.renderer.draw(world, location)
                    }
                }

                /*var chunkCache = renderCache[chunk]
                if (chunkCache == null)
                {
                    chunkCache = arrayOfNulls(16)
                    for (section in 0..15)
                    {
                        val blockTypes = sections[section] ?: continue
                        val sectionCache = BooleanArray(4096)
                        chunkCache[section] = sectionCache
                        for (index in 0..4095)
                        {
                            val blockType = blockTypes[index] ?: continue
                            val location = blockLocationFromSectionIndex(chunk, section, index)
                            sectionCache[index] = blockType.renderer.draw(world, location)
                        }
                    }
                    renderCache[chunk] = chunkCache
                }
                else
                {
                    for (section in 0..15)
                    {
                        val blockTypes = sections[section] ?: continue
                        val sectionCache = chunkCache[section]
                        for (index in 0..4095)
                        {
                            if (sectionCache!![index])
                            {
                                val blockType = blockTypes[index]
                                if (blockType == null || blockType.isHidden) continue
                                val location = blockLocationFromSectionIndex(chunk, section, index)
                                blockType.renderer.draw(world, location)
                            }
                        }
                    }
                }*/
                //for (Entity entity : chunk.getEntities())
//    entity.getType().getRenderer().draw(entity);
            }
        }
    }

    fun refreshRenderCache()
    {
        renderCache.clear()
    }

}
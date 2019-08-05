package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.renderer.Renderer
import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.common.world.WorldRenderer
import me.danetnaverno.editoni.minecraft.util.location.fromSectionIndex
import me.danetnaverno.editoni.texture.Texture
import me.danetnaverno.editoni.util.Camera
import me.danetnaverno.editoni.util.location.BlockLocation
import org.lwjgl.opengl.GL11

class MinecraftWorldRenderer(world: MinecraftWorld) : WorldRenderer(world)
{
    private val mcWorld: MinecraftWorld get() = world as MinecraftWorld
    private val renderCache = mutableMapOf<Chunk, Array<Array<Boolean>?>>()
    private var visibleRegions = listOf<MinecraftRegion>()

    private fun filterVisibleRegions(x: Int, z: Int, x1: Int, z1: Int): Boolean
    {
        val dx = x - (x1 shr 10)
        val dz = z - (z1 shr 10)
        return dx * dx + dz * dz <= 4
    }

    override fun render()
    {
        val cam = BlockLocation(Camera.x.toInt(), 200, Camera.z.toInt())

        val newVisibleRegions = mcWorld.regions.filter {
            filterVisibleRegions(it.location.x, it.location.z, cam.globalX, cam.globalZ)
        }
        for (region in visibleRegions.filterNot { newVisibleRegions.contains(it) })
            region.unload()
        visibleRegions = newVisibleRegions

        for (region in visibleRegions)
        {
            if (region.getLoadedChunks().isEmpty())
                region.loadAllChunks()
            val chunks = region.getLoadedChunks().filter { it.location.distance(cam.toChunkLocation()) <= 5 }

            for (chunk in chunks)
            {
                if ((chunk.location.x + chunk.location.z) % 2 == 0)
                    Texture[ResourceLocation("common","chunk_a")].bind()
                else
                    Texture[ResourceLocation("common","chunk_b")].bind()
                GL11.glBegin(GL11.GL_QUADS)
                GL11.glVertex3i(chunk.location.x * 16, 0, chunk.location.z * 16)
                GL11.glVertex3i(chunk.location.x * 16, 0, (chunk.location.z + 1) * 16)
                GL11.glVertex3i((chunk.location.x + 1) * 16, 0, (chunk.location.z + 1) * 16)
                GL11.glVertex3i((chunk.location.x + 1) * 16, 0, chunk.location.z  * 16)
                GL11.glEnd()

                val sections = chunk.blockTypes
                var chunkCache = renderCache[chunk]

                if (chunkCache == null)
                {
                    chunkCache = Array(16) { null }
                    for (section in 0..15)
                    {
                        val blockTypes = sections[section] ?: continue
                        val sectionCache = Array(4096) { false }
                        chunkCache[section] = sectionCache
                        for (index in 0..4095)
                        {
                            val blockType = blockTypes[index] ?: continue
                            val location = BlockLocation.fromSectionIndex(chunk, section, index)
                            sectionCache[index] = blockType.renderer.draw(world, location)
                        }
                    }
                    renderCache[chunk] = chunkCache
                }

                for (section in 0..15)
                {
                    val blockTypes = sections[section] ?: continue
                    val sectionCache = chunkCache[section]!!
                    for (index in 0..4095)
                    {
                        if (sectionCache[index])
                        {
                            val blockType = blockTypes[index] ?: continue
                            val location = BlockLocation.fromSectionIndex(chunk, section, index)
                            blockType.renderer.draw(world, location)
                        }
                    }
                }

                for (entity in chunk.entities)
                    entity.type.renderer.draw(entity)
            }

            Renderer.draw()
        }
    }

    override fun refreshRenderCache()
    {
        renderCache.clear()
    }
}

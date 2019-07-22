package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.renderer.Renderer
import me.danetnaverno.editoni.common.world.WorldRenderer
import me.danetnaverno.editoni.minecraft.util.location.fromSectionIndex
import me.danetnaverno.editoni.util.Camera
import me.danetnaverno.editoni.util.location.BlockLocation
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.Future

class MinecraftWorldRenderer(world: MinecraftWorld) : WorldRenderer(world)
{
    val mcWorld :MinecraftWorld get() = world as MinecraftWorld
    val executor = ForkJoinPool()

    fun ass(x: Int, z: Int, x1: Int, z1: Int) : Boolean
    {
        val dx = x - (x1 shr 10)
        val dz = z - (z1 shr 10)
        return dx * dx + dz * dz <= 4
    }

    override fun render()
    {
        val cam = BlockLocation(Camera.x.toInt(), 200, Camera.z.toInt())
        val aa = mcWorld.regions.filter { ass(it.location.x, it.location.z, cam.globalX, cam.globalZ) }
        for (region in aa)
        {
            if (region.getLoadedChunks().isEmpty())
                region.loadAllChunks()
            val chunks = region.getLoadedChunks().filter { it.location.distance(cam.toChunkLocation()) <= 5 }
            val futures = arrayListOf<Future<*>>()

            for (chunk in chunks)
            {
                futures.add(executor.submit {
                    val sections = chunk.blockTypes
                    for (section in 0..15)
                    {
                        val blockTypes = sections[section] ?: continue
                        for (index in 0..4095)
                        {
                            val blockType = blockTypes[index] ?: continue
                            val location = BlockLocation.fromSectionIndex(chunk, section, index)
                            blockType.renderer.draw(world, location)
                        }
                    }

                    for (entity in chunk.entities)
                        entity.type.renderer.draw(entity)
                })
            }
            for (future in futures)
                future.get()
            Renderer.draw()
        }
    }
}

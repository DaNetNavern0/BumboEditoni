package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.world.WorldRenderer
import me.danetnaverno.editoni.minecraft.util.location.fromSectionIndex
import me.danetnaverno.editoni.util.Camera
import me.danetnaverno.editoni.util.RobertoGarbagio
import me.danetnaverno.editoni.util.location.BlockLocation

class MinecraftWorldRenderer(world: MinecraftWorld) : WorldRenderer(world)
{
    val mcWorld :MinecraftWorld get() = world as MinecraftWorld

    fun ass(x: Int, z: Int, x1: Int, z1: Int) : Boolean
    {
        val dx = x - (x1 shr 10)
        val dz = z - (z1 shr 10)
        return dx * dx + dz * dz <= 4
    }

    override fun render()
    {
        val now = System.currentTimeMillis()
        val cam = BlockLocation(Camera.x.toInt(), 200, Camera.z.toInt())
        val aa = mcWorld.regions.filter { ass(it.location.x, it.location.z, cam.globalX, cam.globalZ) }
        for (region in aa)
        {
            if (region.getLoadedChunks().isEmpty())
                region.loadAllChunks()
            val chunks = region.getLoadedChunks().filter { it.location.distance(cam.toChunkLocation()) <= 5 }
            for (chunk in chunks)
            {
                val sections = chunk.blockTypes
                for (section in 0..15)
                {
                    val blockTypes = sections[section] ?: continue
                    for (index in 0..4095)
                    {
                        val blockType = blockTypes[index] ?: continue
                        val location = BlockLocation.fromSectionIndex(chunk, section, index)
                        blockType.renderer.draw(chunk, location)
                    }
                }
            }

            for (chunk in region.getLoadedChunks())
            {
                for (entity in chunk.entities)
                    entity.type.renderer.draw(entity)
            }
        }
        RobertoGarbagio.logger.info("tick: ${(System.currentTimeMillis() - now)}")
    }
}

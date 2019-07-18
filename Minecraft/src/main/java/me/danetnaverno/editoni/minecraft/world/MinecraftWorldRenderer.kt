package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.world.WorldRenderer
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
        return dx * dx + dz * dz < 3
    }

    override fun render()
    {
        val now = System.currentTimeMillis()
        val cam = BlockLocation(Camera.x.toInt(), 200, Camera.z.toInt())
        val aa = mcWorld.regions.filter { ass(it.x, it.z, cam.globalX, cam.globalZ) }
        for (region in aa)
        {
            mcWorld.loadChunkAt(cam.toChunkLocation())
            val chunks = region.getLoadedChunks()
            for (chunk in chunks)
            {
                if (!chunk.isLoaded)
                    continue

                for (block in chunk.blocks)
                    block.type.renderer.draw(block)
            }

            for (chunk in region.getLoadedChunks())
            {
                if (!chunk.isLoaded)
                    continue
                for (entity in chunk.entities)
                    entity.type.renderer.draw(entity)
            }
        }
        RobertoGarbagio.logger.info("tick: ${(System.currentTimeMillis() - now)}")
    }
}

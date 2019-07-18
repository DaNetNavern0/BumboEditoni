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
        return dx * dx + dz * dz < 1
    }

    override fun render()
    {
        val now = System.currentTimeMillis()
        val cam = BlockLocation(Camera.x.toInt(), 200, Camera.z.toInt())
        val aa = mcWorld.regions.filter { ass(it.x, it.z, cam.globalX, cam.globalZ) }
        for (region in aa)
        {
            if (region.getLoadedChunks().isEmpty())
                region.loadAllChunks()
            //mcWorld.loadChunkAt(cam.toChunkLocation())
            val chunks = region.getLoadedChunks().filter { it.location.distance(cam.toChunkLocation()) <= 5 }
            for (chunk in chunks)
            {
                for(x in 0..15)
                    for(z in 0..15)
                        for(y in 0..255)
                        {
                            val location = BlockLocation(chunk, x, y, z)
                            val blockState = chunk.getBlockStateAt(x, y, z) ?: continue
                            blockState.getType().renderer.draw(location, blockState)
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

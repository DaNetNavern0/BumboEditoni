package me.danetnaverno.editoni.render.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.world.World
import java.nio.FloatBuffer

class BlockRendererAir : BlockRenderer()
{
    override fun fromJson(data: JSONObject)
    {
    }

    override fun isVisible(world: World, location: BlockLocation.Mutable): Boolean
    {
        return false
    }

    override fun getMaxVertexCount(): Int
    {
        return 0
    }

    override fun bake(world: World, location: BlockLocation.Mutable, vertexBuffer: FloatBuffer)
    {
    }

    override fun shouldRenderSideAgainst(world: World, location: BlockLocation): Boolean
    {
        return false
    }
}

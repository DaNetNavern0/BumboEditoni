package me.danetnaverno.editoni.render.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.location.BlockLocationMutable
import me.danetnaverno.editoni.world.World
import java.nio.FloatBuffer

abstract class BlockRenderer
{
    abstract fun fromJson(data: JSONObject)
    abstract fun isVisible(world: World, location: BlockLocationMutable): Boolean
    abstract fun getMaxVertexCount() : Int
    abstract fun bake(world: World, location: BlockLocationMutable, vertexBuffer: FloatBuffer)

    open fun shouldRenderSideAgainst(world: World, location: BlockLocationMutable): Boolean
    {
        if (location.localY < 0 || location.localY > 255)
            return true
        val chunk = world.getChunkIfLoaded(location) ?: return true
        val type = chunk.getBlockTypeAt(location) ?: return true
        return !type.isOpaque
    }
}

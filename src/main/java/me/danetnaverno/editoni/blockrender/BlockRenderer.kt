package me.danetnaverno.editoni.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.world.World

abstract class BlockRenderer
{
    abstract fun fromJson(data: JSONObject)
    abstract fun isVisible(world: World, location: BlockLocation.Mutable): Boolean
    abstract fun draw(world: World, location: BlockLocation, vertexBuffer: ArrayList<Float>, textureBuffer: ArrayList<Float>)

    open fun shouldRenderSideAgainst(world: World, location: BlockLocation): Boolean
    {
        if (location.localY < 0 || location.localY > 255)
            return true
        val chunk = world.getChunkIfLoaded(location.toChunkLocation()) ?: return true
        val type = chunk.getBlockTypeAt(location) ?: return true
        return !type.isOpaque
    }
}

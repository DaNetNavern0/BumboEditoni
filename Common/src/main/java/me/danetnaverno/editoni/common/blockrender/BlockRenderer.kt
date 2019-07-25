package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.util.location.BlockLocation

abstract class BlockRenderer
{
    abstract fun fromJson(data: JSONObject)

    abstract fun draw(world: World, location: BlockLocation)

    open fun shouldRenderSideAgainst(world: World, location: BlockLocation): Boolean
    {
        if (location.localY < 0 || location.localY > 255)
            return true
        val chunk = world.getChunkIfLoaded(location.toChunkLocation()) ?: return false
        val type = chunk.getBlockTypeAt(location) ?: return true
        return !type.isOpaque
    }
}

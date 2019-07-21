package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.util.location.BlockLocation

abstract class BlockRenderer
{
    abstract fun fromJson(data: JSONObject)

    abstract fun draw(chunk: Chunk, location: BlockLocation)

    open fun shouldRenderSideAgainst(chunk: Chunk, location: BlockLocation): Boolean
    {
        if (location.localY < 0 || location.localY > 256)
            return true
        val otherChunk = chunk.world.getChunkIfLoaded(location.toChunkLocation()) ?: return false
        val type = otherChunk.getBlockTypeAt(location) ?: return true
        return !type.isOpaque
    }
}

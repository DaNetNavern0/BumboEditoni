package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.util.location.BlockLocation

class BlockRendererAir : BlockRenderer()
{
    override fun fromJson(data: JSONObject)
    {
    }

    override fun draw(chunk: Chunk, location: BlockLocation)
    {
    }

    override fun shouldRenderSideAgainst(chunk: Chunk, location: BlockLocation): Boolean
    {
        return false
    }
}

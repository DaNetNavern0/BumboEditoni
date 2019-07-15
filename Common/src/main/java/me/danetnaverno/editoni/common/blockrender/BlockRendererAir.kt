package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.world.Block

class BlockRendererAir : BlockRenderer()
{
    override fun fromJson(data: JSONObject)
    {
    }

    override fun draw(block: Block)
    {
    }

    override fun shouldRenderSideAgainst(block: Block): Boolean
    {
        return false
    }
}

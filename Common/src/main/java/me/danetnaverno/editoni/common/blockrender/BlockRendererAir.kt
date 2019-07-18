package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.BlockState
import me.danetnaverno.editoni.util.location.BlockLocation

class BlockRendererAir : BlockRenderer()
{
    override fun fromJson(data: JSONObject)
    {
    }

    override fun draw(location: BlockLocation, blockState: BlockState)
    {
    }

    override fun shouldRenderSideAgainst(block: Block?): Boolean
    {
        return false
    }
}

package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.util.location.BlockLocation

class BlockRendererAir : BlockRenderer()
{
    override fun fromJson(data: JSONObject)
    {
    }

    override fun draw(world: World, location: BlockLocation) : Boolean
    {
        return false
    }

    override fun shouldRenderSideAgainst(world: World, location: BlockLocation): Boolean
    {
        return false
    }
}

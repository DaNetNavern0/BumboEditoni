package me.danetnaverno.editoni.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.blockrender.BlockRenderer
import me.danetnaverno.editoni.world.World
import me.danetnaverno.editoni.util.location.BlockLocation

class BlockRendererAir : BlockRenderer()
{
    override fun fromJson(data: JSONObject)
    {
    }

    override fun draw(world: World, location: BlockLocation): Boolean
    {
        return false
    }

    override fun shouldRenderSideAgainst(world: World, location: BlockLocation): Boolean
    {
        return false
    }
}

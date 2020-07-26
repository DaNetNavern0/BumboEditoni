package me.danetnaverno.editoni.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.world.World
import me.danetnaverno.editoni.util.location.BlockLocation

class BlockRendererAir : BlockRenderer()
{
    override fun fromJson(data: JSONObject)
    {
    }

    override fun isVisible(world: World, location: BlockLocation.Mutable): Boolean
    {
        return false
    }

    override fun draw(world: World, location: BlockLocation)
    {
    }

    override fun shouldRenderSideAgainst(world: World, location: BlockLocation): Boolean
    {
        return false
    }
}

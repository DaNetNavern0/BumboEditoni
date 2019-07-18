package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.BlockState
import me.danetnaverno.editoni.util.location.BlockLocation

abstract class BlockRenderer
{
    abstract fun fromJson(data: JSONObject)

    abstract fun draw(location: BlockLocation, blockState: BlockState)

    open fun shouldRenderSideAgainst(block: Block?): Boolean
    {
        return block==null || block.type.isOpaque
    }
}

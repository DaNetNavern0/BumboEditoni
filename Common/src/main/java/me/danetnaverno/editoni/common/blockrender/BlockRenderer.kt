package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.world.Block

abstract class BlockRenderer
{
    abstract fun fromJson(data: JSONObject)

    abstract fun draw(block: Block)
}

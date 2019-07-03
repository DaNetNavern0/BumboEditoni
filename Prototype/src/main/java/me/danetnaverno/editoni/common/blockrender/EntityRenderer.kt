package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.world.Entity

abstract class EntityRenderer
{
    abstract fun fromJson(data: JSONObject)

    abstract fun draw(entity: Entity)
}

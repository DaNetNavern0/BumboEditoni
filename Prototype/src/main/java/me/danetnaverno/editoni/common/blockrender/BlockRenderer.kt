package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject

abstract class BlockRenderer
{
    abstract fun fromJson(data: JSONObject)

    abstract fun draw()
}

package me.danetnaverno.editoni.common.render

import com.alibaba.fastjson.JSONObject

abstract class BlockRenderer
{
    abstract fun fromJson(data: JSONObject)

    abstract fun draw()
}

package me.danetnaverno.editoni.render.blockrender

import com.alibaba.fastjson.JSONObject

object BlockRendererDictionary
{
    private val renderers = mutableMapOf<String, Class<out BlockRenderer>>()

    fun register(id: String, type: Class<out BlockRenderer>)
    {
        renderers[id] = type
    }

    fun create(data: JSONObject): BlockRenderer
    {
        val name = data.getString("type")
        var rendererClass = renderers[name]
        if (rendererClass == null)
            rendererClass = BlockRendererCube::class.java
        val constr = rendererClass.getConstructor()
        val instance = constr.newInstance() as BlockRenderer
        instance.fromJson(data)
        return instance
    }
}
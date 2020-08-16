package me.danetnaverno.editoni.render.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.util.ResourceLocation

object BlockRendererDictionary
{
    private val renderers = mutableMapOf<ResourceLocation, Class<out BlockRenderer>>()

    fun register(id: ResourceLocation, type: Class<out BlockRenderer>)
    {
        renderers[id] = type
    }

    fun create(data: JSONObject): BlockRenderer
    {
        val name = ResourceLocation(data.getString("type"))
        var rendererClass = renderers[name]
        if (rendererClass == null)
            rendererClass = BlockRendererCube::class.java
        //requireNotNull(rendererClass) { "Renderer type '$name' not found!" }
        val constr = rendererClass.getConstructor()
        val instance = constr.newInstance() as BlockRenderer
        instance.fromJson(data)
        return instance
    }
}
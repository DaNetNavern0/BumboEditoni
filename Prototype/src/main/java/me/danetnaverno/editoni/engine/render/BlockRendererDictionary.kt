package me.danetnaverno.editoni.engine.render

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.engine.texture.TextureDictionary

object BlockRendererDictionary
{
    @JvmField
    val ERROR = BlockRendererCube(TextureDictionary["error"])

    private val renderers = mutableMapOf<ResourceLocation, Class<out BlockRenderer>>()

    @JvmStatic
    fun register(id: ResourceLocation, type: Class<out BlockRenderer>)
    {
        renderers[id] = type
    }

    fun create(data: JSONObject): BlockRenderer
    {
        val name = ResourceLocation(data.getString("type"))
        val rendererClass = renderers[name]
        requireNotNull(rendererClass) { "Renderer type '$name' not found!" }
        val constr = rendererClass!!.getConstructor()
        val instance = constr.newInstance() as BlockRenderer
        instance.fromJson(data)
        return instance
    }
}
package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.ResourceLocation

object EntityRendererDictionary
{
    val DEFAULT: EntityRenderer = EntityRendererDefault()

    private val renderers = mutableMapOf<ResourceLocation, Class<out EntityRenderer>>()

    @JvmStatic
    fun register(id: ResourceLocation, type: Class<out EntityRenderer>)
    {
        renderers[id] = type
    }

    fun create(data: JSONObject): EntityRenderer
    {
        val name = ResourceLocation(data.getString("type"))
        val rendererClass = renderers[name]
        requireNotNull(rendererClass) { "Entity renderer type '$name' not found!" }
        val constr = rendererClass.getConstructor()
        val instance = constr.newInstance() as EntityRenderer
        instance.fromJson(data)
        return instance
    }
}
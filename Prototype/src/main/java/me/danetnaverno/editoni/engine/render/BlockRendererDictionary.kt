package me.danetnaverno.editoni.engine.render

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.engine.texture.TextureDictionary

object BlockRendererDictionary
{
    @JvmField
    val ERROR = BlockRendererCube(TextureDictionary["error"])

    private val renderers = mutableMapOf<String, Class<out BlockRenderer>>()

    init
    {
        renderers["air"] = BlockRendererAir::class.java
        renderers["cube"] = BlockRendererCube::class.java
        renderers["chest"] = BlockRendererChest::class.java
    }

    fun create(data: JSONObject): BlockRenderer
    {
        val name = data.getString("type")
        val rendererClass = renderers[name]
        checkNotNull(rendererClass) { "Renderer type '$name' not found!" }
        try
        {
            val constr = rendererClass!!.getConstructor()
            val instance = constr.newInstance() as BlockRenderer
            instance.fromJson(data)
            return instance
        }
        catch (e: Exception)
        {
            checkNotNull(rendererClass) { "Renderer type '$name' has no empty constructor!" }
            throw e
        }
    }
}

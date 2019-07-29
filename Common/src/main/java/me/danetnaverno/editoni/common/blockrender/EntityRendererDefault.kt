package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.renderer.Renderer
import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.texture.Texture

open class EntityRendererDefault : EntityRenderer()
{
    override fun fromJson(data: JSONObject)
    {
    }

    open fun getSize(): Float
    {
        return 1.0f
    }

    override fun draw(entity: Entity)
    {
        val size = getSize()

        val location = entity.location.add(-0.5, 0.0, -0.5)

        Renderer.addObject(texture.id, doubleArrayOf(location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f,
                location.globalX + 0.0f, location.globalY + size, location.globalZ + size,
                location.globalX + size, location.globalY + size, location.globalZ + size,
                location.globalX + size, location.globalY + size, location.globalZ + 0.0f))

        Renderer.addObject(texture.id, doubleArrayOf(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f,
                location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f,
                location.globalX + size, location.globalY + 0.0f, location.globalZ + size,
                location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size))

        Renderer.addObject(texture.id, doubleArrayOf(location.globalX + size, location.globalY + size, location.globalZ + size,
                location.globalX + 0.0f, location.globalY + size, location.globalZ + size,
                location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size,
                location.globalX + size, location.globalY + 0.0f, location.globalZ + size))

        Renderer.addObject(texture.id, doubleArrayOf(location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f,
                location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f,
                location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f,
                location.globalX + size, location.globalY + size, location.globalZ + 0.0f))

        Renderer.addObject(texture.id, doubleArrayOf(location.globalX + 0.0f, location.globalY + size, location.globalZ + size,
                location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f,
                location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f,
                location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size))

        Renderer.addObject(texture.id, doubleArrayOf(location.globalX + size, location.globalY + size, location.globalZ + 0.0f,
                location.globalX + size, location.globalY + size, location.globalZ + size,
                location.globalX + size, location.globalY + 0.0f, location.globalZ + size,
                location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f))
    }

    companion object
    {
        private val texture = Texture[ResourceLocation("minecraft:entity")]
    }
}

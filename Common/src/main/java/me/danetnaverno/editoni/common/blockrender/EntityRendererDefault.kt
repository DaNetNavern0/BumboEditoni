package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.texture.Texture
import org.lwjgl.opengl.GL11

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

        texture.bind()
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + size, location.globalZ + size)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + size, location.globalZ + size)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + size, location.globalZ + 0.0f)

        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + 0.0f, location.globalZ + size)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size)

        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + size, location.globalZ + size)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + size, location.globalZ + size)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + 0.0f, location.globalZ + size)

        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + size, location.globalZ + 0.0f)

        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + size, location.globalZ + size)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size)

        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + size, location.globalZ + 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + size, location.globalZ + size)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + 0.0f, location.globalZ + size)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f)
        GL11.glEnd()
    }

    companion object
    {
        private val texture = Texture[ResourceLocation("minecraft:entity")]
    }
}

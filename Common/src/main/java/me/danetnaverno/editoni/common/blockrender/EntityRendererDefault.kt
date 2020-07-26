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
        GL11.glVertex3d(x1, y2, z1)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(x1, y2, z2)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(x2, y2, z2)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(x2, y2, z1)

        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3d(x1, y1, z1)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(x2, y1, z1)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(x2, y1, z2)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(x1, y1, z2)

        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3d(x2, y2, z2)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(x1, y2, z2)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(x1, y1, z2)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(x2, y1, z2)

        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3d(x2, y1, z1)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(x1, y1, z1)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(x1, y2, z1)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(x2, y2, z1)

        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3d(x1, y2, z2)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(x1, y2, z1)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(x1, y1, z1)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(x1, y1, z2)

        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3d(x2, y2, z1)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3d(x2, y2, z2)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3d(x2, y1, z2)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3d(x2, y1, z1)
        GL11.glEnd()
    }

    companion object
    {
        private val texture = Texture[ResourceLocation("minecraft:entity")]
    }
}

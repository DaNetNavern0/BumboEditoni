package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.engine.texture.Texture
import org.lwjgl.opengl.GL11

class EntityRendererDefault : EntityRenderer()
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

        texture.bind()
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(0.0f, size, 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(0.0f, size, size)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(size, size, size)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(size, size, 0.0f)
        GL11.glEnd()

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(0.0f, 0.0f, 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(size, 0.0f, 0.0f)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(size, 0.0f, size)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(0.0f, 0.0f, size)
        GL11.glEnd()

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(size, size, size)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(0.0f, size, size)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(0.0f, 0.0f, size)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(size, 0.0f, size)
        GL11.glEnd()

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(size, 0.0f, 0.0f)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(0.0f, 0.0f, 0.0f)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(0.0f, size, 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(size, size, 0.0f)
        GL11.glEnd()

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(0.0f, size, size)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(0.0f, size, 0.0f)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(0.0f, 0.0f, 0.0f)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(0.0f, 0.0f, size)
        GL11.glEnd()

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(size, size, 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(size, size, size)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(size, 0.0f, size)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(size, 0.0f, 0.0f)
        GL11.glEnd()
    }

    companion object
    {
        private val texture = Texture["minecraft/entity"]
    }
}

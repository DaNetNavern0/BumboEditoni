package me.danetnaverno.editoni.minecraft.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.BlockRenderer
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.minecraft.blockstate.MinecraftLiquidState
import me.danetnaverno.editoni.texture.Texture
import me.danetnaverno.editoni.util.location.BlockLocation
import org.lwjgl.opengl.GL11

class BlockRendererMinecraftLiquid : BlockRenderer()
{
    private lateinit var texture: Texture

    override fun fromJson(data: JSONObject)
    {
        texture = Texture[ResourceLocation(data.getString("texture"))]
    }

    override fun draw(world: World, location: BlockLocation): Boolean
    {
        val state = world.getBlockStateAt(location)
        val height = (8 - (state as MinecraftLiquidState).level) / 8f - 0.1f
        var isVisible = false

        texture.bind()
        if (shouldRenderSideAgainst(world, location.add(0, 1, 0)))
        {
            isVisible = true

            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + height, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + height, location.globalZ + 1.0f)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + height, location.globalZ + 1.0f)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + height, location.globalZ + 0.0f)
            GL11.glEnd()
        }

        if (shouldRenderSideAgainst(world, location.add(0, -1, 0)))
        {
            isVisible = true
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 1.0f)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 1.0f)
            GL11.glEnd()
        }

        if (shouldRenderSideAgainst(world, location.add(0, 0, 1)))
        {
            isVisible = true
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + height, location.globalZ + 1.0f)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + height, location.globalZ + 1.0f)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 1.0f)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 1.0f)
            GL11.glEnd()
        }

        if (shouldRenderSideAgainst(world, location.add(0, 0, -1)))
        {
            isVisible = true
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + height, location.globalZ + 0.0f)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + height, location.globalZ + 0.0f)
            GL11.glEnd()
        }

        if (shouldRenderSideAgainst(world, location.add(-1, 0, 0)))
        {
            isVisible = true
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + height, location.globalZ + 1.0f)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + height, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 1.0f)
            GL11.glEnd()
        }

        if (shouldRenderSideAgainst(world, location.add(1, 0, 0)))
        {
            isVisible = true
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + height, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + height, location.globalZ + 1.0f)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 1.0f)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glEnd()
        }

        return isVisible
    }
}

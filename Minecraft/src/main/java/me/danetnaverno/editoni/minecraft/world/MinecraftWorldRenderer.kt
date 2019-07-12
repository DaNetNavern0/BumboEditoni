package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.blockrender.BlockRendererAir
import me.danetnaverno.editoni.common.world.WorldRenderer
import org.lwjgl.opengl.GL11

class MinecraftWorldRenderer(world: MinecraftWorld) : WorldRenderer(world)
{
    val mcWorld :MinecraftWorld get() = super.world as MinecraftWorld

    override fun render()
    {
        for (region in mcWorld.regions)
        {
            GL11.glPushMatrix()
            GL11.glTranslatef((region.x shl 9).toFloat(), 0f, (region.z shl 9).toFloat())
            for (chunk in region.getChunks())
            {
                GL11.glPushMatrix()
                GL11.glTranslatef((chunk.renderX shl 4).toFloat(), 0f, (chunk.renderZ shl 4).toFloat())

                for (block in chunk.blocks)
                {
                    if (block.type.renderer !is BlockRendererAir) //todo hidden blocks are not hidden
                    {
                        GL11.glPushMatrix()
                        GL11.glTranslatef(block.localPos.x.toFloat(), block.localPos.y.toFloat(), block.localPos.z.toFloat())
                        block.type.renderer.draw(block)
                        GL11.glPopMatrix()
                    }
                }
                GL11.glPopMatrix()
            }
            GL11.glPopMatrix()

            for (chunk in region.getChunks())
            {
                for (entity in chunk.entities)
                {
                    GL11.glPushMatrix()
                    GL11.glTranslated(entity.globalPos.x - 0.5, entity.globalPos.y, entity.globalPos.z - 0.5)
                    entity.type.renderer.draw(entity)
                    GL11.glPopMatrix()
                }
            }
        }
    }
}

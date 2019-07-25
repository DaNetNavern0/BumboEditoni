package me.danetnaverno.editoni.minecraft.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.blockrender.BlockRenderer
import me.danetnaverno.editoni.common.renderer.Renderer
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.minecraft.blockstate.MinecraftLiquidState
import me.danetnaverno.editoni.texture.Texture
import me.danetnaverno.editoni.util.location.BlockLocation

class BlockRendererMinecraftLiquid : BlockRenderer()
{
    private lateinit var texture: Texture

    override fun fromJson(data: JSONObject)
    {
        texture = Texture[ResourceLocation(data.getString("texture"))]
    }

    override fun draw(world: World, location: BlockLocation)
    {
        val state = world.getBlockStateAt(location)
        val height = (8 - (state as MinecraftLiquidState).level) / 8f - 0.1f

        if (shouldRenderSideAgainst(world, location.add(0, 1, 0)))
        {
            Renderer.addObject(texture.id, floatArrayOf(location.globalX + 0.0f, location.globalY + height, location.globalZ + 0.0f,
                    location.globalX + 0.0f, location.globalY + height, location.globalZ + 1.0f,
                    location.globalX + 1.0f, location.globalY + height, location.globalZ + 1.0f,
                    location.globalX + 1.0f, location.globalY + height, location.globalZ + 0.0f))
        }

        if (shouldRenderSideAgainst(world, location.add(0, -1, 0)))
        {
            Renderer.addObject(texture.id, floatArrayOf(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f,
                    location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 0.0f,
                    location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 1.0f,
                    location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 1.0f))
        }

        if (shouldRenderSideAgainst(world, location.add(0, 0, 1)))
        {
            Renderer.addObject(texture.id, floatArrayOf(location.globalX + 1.0f, location.globalY + height, location.globalZ + 1.0f,
                    location.globalX + 0.0f, location.globalY + height, location.globalZ + 1.0f,
                    location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 1.0f,
                    location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 1.0f))
        }

        if (shouldRenderSideAgainst(world, location.add(0, 0, -1)))
        {
            Renderer.addObject(texture.id, floatArrayOf(location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 0.0f,
                    location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f,
                    location.globalX + 0.0f, location.globalY + height, location.globalZ + 0.0f,
                    location.globalX + 1.0f, location.globalY + height, location.globalZ + 0.0f))
        }

        if (shouldRenderSideAgainst(world, location.add(1, 0, 0)))
        {
            Renderer.addObject(texture.id, floatArrayOf(location.globalX + 0.0f, location.globalY + height, location.globalZ + 1.0f,
                    location.globalX + 0.0f, location.globalY + height, location.globalZ + 0.0f,
                    location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f,
                    location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 1.0f))
        }

        if (shouldRenderSideAgainst(world, location.add(-1, 0, 0)))
        {
            Renderer.addObject(texture.id, floatArrayOf(location.globalX + 1.0f, location.globalY + height, location.globalZ + 0.0f,
                    location.globalX + 1.0f, location.globalY + height, location.globalZ + 1.0f,
                    location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 1.0f,
                    location.globalX + 1.0f, location.globalY + 0.0f, location.globalZ + 0.0f))
        }
    }
}

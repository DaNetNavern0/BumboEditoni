package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.renderer.Renderer
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.texture.Texture
import me.danetnaverno.editoni.util.location.BlockLocation

open class BlockRendererCube : BlockRenderer
{
    private lateinit var top: Texture
    private lateinit var bottom: Texture
    private lateinit var north: Texture
    private lateinit var west: Texture
    private lateinit var south: Texture
    private lateinit var east: Texture

    constructor()

    constructor(texture: Texture)
    {
        plainTexture(texture)
    }

    constructor(top: Texture, bottom: Texture, side: Texture)
    {
        withSideTexture(top, bottom, side)
    }

    constructor(top: Texture, bottom: Texture, north: Texture, west: Texture, south: Texture, east: Texture)
    {
        sixTextures(top, bottom, north, west, south, east)
    }

    protected fun plainTexture(texture: Texture)
    {
        this.top = texture
        this.bottom = texture
        this.north = texture
        this.west = texture
        this.south = texture
        this.east = texture
    }

    protected fun withSideTexture(top: Texture, bottom: Texture, side: Texture)
    {
        this.top = top
        this.bottom = bottom
        this.north = side
        this.west = side
        this.south = side
        this.east = side
    }

    protected fun sixTextures(top: Texture, bottom: Texture, north: Texture, west: Texture, south: Texture, east: Texture)
    {
        this.top = top
        this.bottom = bottom
        this.north = north
        this.west = west
        this.south = south
        this.east = east
    }

    override fun fromJson(data: JSONObject)
    {
        when
        {
            data.containsKey("texture") -> plainTexture(Texture[ResourceLocation(data.getString("texture"))])
            data.containsKey("side") -> withSideTexture(
                    Texture[ResourceLocation(data.getString("top"))],
                    Texture[ResourceLocation(data.getString("bottom"))],
                    Texture[ResourceLocation(data.getString("side"))])
            data.containsKey("west") -> sixTextures(
                    Texture[ResourceLocation(data.getString("top"))],
                    Texture[ResourceLocation(data.getString("bottom"))],
                    Texture[ResourceLocation(data.getString("north"))],
                    Texture[ResourceLocation(data.getString("west"))],
                    Texture[ResourceLocation(data.getString("south"))],
                    Texture[ResourceLocation(data.getString("east"))])
        }
    }

    open fun getSize(): Float
    {
        return 1.0f
    }

    override fun draw(world: World, location: BlockLocation)
    {
        val size = getSize()

        if (shouldRenderSideAgainst(world, location.add(0, 1, 0)))
        {
            Renderer.addObject(top.id, floatArrayOf(location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f,
                    location.globalX + 0.0f, location.globalY + size, location.globalZ + size,
                    location.globalX + size, location.globalY + size, location.globalZ + size,
                    location.globalX + size, location.globalY + size, location.globalZ + 0.0f))
        }

        if (shouldRenderSideAgainst(world, location.add(0, -1, 0)))
            {
                Renderer.addObject(bottom.id, floatArrayOf(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f,
                location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f,
                location.globalX + size, location.globalY + 0.0f, location.globalZ + size,
                location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size))
        }

        if (shouldRenderSideAgainst(world, location.add(0, 0, 1)))
        {
            Renderer.addObject(south.id, floatArrayOf(location.globalX + size, location.globalY + size, location.globalZ + size,
            location.globalX + 0.0f, location.globalY + size, location.globalZ + size,
            location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size,
            location.globalX + size, location.globalY + 0.0f, location.globalZ + size))
        }

        if (shouldRenderSideAgainst(world, location.add(0, 0, -1)))
        {
            Renderer.addObject(north.id, floatArrayOf(location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f,
            location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f,
            location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f,
            location.globalX + size, location.globalY + size, location.globalZ + 0.0f))
        }

        if (shouldRenderSideAgainst(world, location.add(-1, 0, 0)))
        {
            Renderer.addObject(west.id, floatArrayOf(location.globalX + 0.0f, location.globalY + size, location.globalZ + size,
            location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f,
            location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f,
            location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size))
        }

        if (shouldRenderSideAgainst(world, location.add(1, 0, 0)))
        {
            Renderer.addObject(east.id, floatArrayOf(location.globalX + size, location.globalY + size, location.globalZ + 0.0f,
            location.globalX + size, location.globalY + size, location.globalZ + size,
            location.globalX + size, location.globalY + 0.0f, location.globalZ + size,
            location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f))
        }
    }
}
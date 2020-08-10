package me.danetnaverno.editoni.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.texture.Texture
import me.danetnaverno.editoni.texture.TextureAtlas
import me.danetnaverno.editoni.util.ResourceLocation
import me.danetnaverno.editoni.world.World
import java.nio.FloatBuffer

open class BlockRendererCube : BlockRenderer
{
    private lateinit var top: Texture
    private lateinit var bottom: Texture
    private lateinit var north: Texture
    private lateinit var west: Texture
    private lateinit var south: Texture
    private lateinit var east: Texture

    constructor()

    @Suppress("unused")
    constructor(texture: Texture)
    {
        plainTexture(texture)
    }

    @Suppress("unused")
    constructor(top: Texture, bottom: Texture, side: Texture)
    {
        withSideTexture(top, bottom, side)
    }

    @Suppress("unused")
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

    override fun isVisible(world: World, location: BlockLocation.Mutable): Boolean
    {
        //A small trick to avoid creating new short-living BlockLocation instances
        val x = location.globalX
        val y = location.globalY
        val z = location.globalZ
        val result = shouldRenderSideAgainst(world, location.addMutably(0, 1, 0)) ||
                shouldRenderSideAgainst(world, location.addMutably(0, -2, 0)) ||
                shouldRenderSideAgainst(world, location.addMutably(0, 1, 1)) ||
                shouldRenderSideAgainst(world, location.addMutably(0, 0, -2)) ||
                shouldRenderSideAgainst(world, location.addMutably(1, 0, 1)) ||
                shouldRenderSideAgainst(world, location.addMutably(-2, 0, 0))
        location.setMutably(x, y, z)
        return result
    }


    override fun draw(world: World, location: BlockLocation, vertexBuffer: FloatBuffer, uvBuffer: FloatBuffer)
    {
        val size = getSize()
        val x1 = location.globalX.toFloat()
        val y1 = location.globalY.toFloat()
        val z1 = location.globalZ.toFloat()

        val x2 = location.globalX + size
        val y2 = location.globalY + size
        val z2 = location.globalZ + size

        if (shouldRenderSideAgainst(world, location.add(0, 1, 0)))
        {
            val tIndex = TextureAtlas.mainAtlas.getZLayer(top)
            vertexBuffer.put(x1)
            vertexBuffer.put(y2)
            vertexBuffer.put(z1)

            uvBuffer.put(0f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x1)
            vertexBuffer.put(y2)
            vertexBuffer.put(z2)

            uvBuffer.put(1f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x2)
            vertexBuffer.put(y2)
            vertexBuffer.put(z2)

            uvBuffer.put(1f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x2)
            vertexBuffer.put(y2)
            vertexBuffer.put(z1)

            uvBuffer.put(0f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)
        }

        if (shouldRenderSideAgainst(world, location.add(0, -1, 0)))
        {
            val tIndex = TextureAtlas.mainAtlas.getZLayer(bottom)
            vertexBuffer.put(x1)
            vertexBuffer.put(y1)
            vertexBuffer.put(z1)

            uvBuffer.put(0f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x2)
            vertexBuffer.put(y1)
            vertexBuffer.put(z1)

            uvBuffer.put(1f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x2)
            vertexBuffer.put(y1)
            vertexBuffer.put(z2)

            uvBuffer.put(1f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x1)
            vertexBuffer.put(y1)
            vertexBuffer.put(z2)

            uvBuffer.put(0f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)
        }

        if (shouldRenderSideAgainst(world, location.add(0, 0, 1)))
        {
            val tIndex = TextureAtlas.mainAtlas.getZLayer(south)

            vertexBuffer.put(x2)
            vertexBuffer.put(y2)
            vertexBuffer.put(z2)

            uvBuffer.put(0f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x1)
            vertexBuffer.put(y2)
            vertexBuffer.put(z2)

            uvBuffer.put(1f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x1)
            vertexBuffer.put(y1)
            vertexBuffer.put(z2)

            uvBuffer.put(1f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x2)
            vertexBuffer.put(y1)
            vertexBuffer.put(z2)

            uvBuffer.put(0f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)
        }

        if (shouldRenderSideAgainst(world, location.add(0, 0, -1)))
        {
            val tIndex = TextureAtlas.mainAtlas.getZLayer(north)

            vertexBuffer.put(x2)
            vertexBuffer.put(y1)
            vertexBuffer.put(z1)

            uvBuffer.put(0f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x1)
            vertexBuffer.put(y1)
            vertexBuffer.put(z1)

            uvBuffer.put(1f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x1)
            vertexBuffer.put(y2)
            vertexBuffer.put(z1)

            uvBuffer.put(1f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x2)
            vertexBuffer.put(y2)
            vertexBuffer.put(z1)

            uvBuffer.put(0f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)
        }

        if (shouldRenderSideAgainst(world, location.add(-1, 0, 0)))
        {
            val tIndex = TextureAtlas.mainAtlas.getZLayer(west)

            vertexBuffer.put(x1)
            vertexBuffer.put(y2)
            vertexBuffer.put(z2)

            uvBuffer.put(0f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x1)
            vertexBuffer.put(y2)
            vertexBuffer.put(z1)

            uvBuffer.put(1f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x1)
            vertexBuffer.put(y1)
            vertexBuffer.put(z1)

            uvBuffer.put(1f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x1)
            vertexBuffer.put(y1)
            vertexBuffer.put(z2)

            uvBuffer.put(0f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)
        }

        if (shouldRenderSideAgainst(world, location.add(1, 0, 0)))
        {
            val tIndex = TextureAtlas.mainAtlas.getZLayer(east)

            vertexBuffer.put(x2)
            vertexBuffer.put(y2)
            vertexBuffer.put(z1)

            uvBuffer.put(0f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x2)
            vertexBuffer.put(y2)
            vertexBuffer.put(z2)

            uvBuffer.put(1f)
            uvBuffer.put(0f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x2)
            vertexBuffer.put(y1)
            vertexBuffer.put(z2)

            uvBuffer.put(1f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)

            vertexBuffer.put(x2)
            vertexBuffer.put(y1)
            vertexBuffer.put(z1)

            uvBuffer.put(0f)
            uvBuffer.put(1f)
            uvBuffer.put(tIndex)
        }
    }
}
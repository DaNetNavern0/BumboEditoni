package me.danetnaverno.editoni.render.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.location.BlockLocationMutable
import me.danetnaverno.editoni.texture.Texture
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

    @Suppress("unused")
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
            data.containsKey("texture") -> plainTexture(Texture[data.getString("texture")])
            data.containsKey("side") -> withSideTexture(
                    Texture[data.getString("top")],
                    Texture[data.getString("bottom")],
                    Texture[data.getString("side")])
            data.containsKey("west") -> sixTextures(
                    Texture[data.getString("top")],
                    Texture[data.getString("bottom")],
                    Texture[data.getString("north")],
                    Texture[data.getString("west")],
                    Texture[data.getString("south")],
                    Texture[data.getString("east")])
        }
    }

    open fun getSize(): Float
    {
        return 1.0f
    }

    //todo this could use some improvements, see [BlockRenderer.shouldRenderSideAgainst]
    override fun isVisible(world: World, location: BlockLocationMutable): Boolean
    {
        //A small trick to avoid creating new short-living BlockLocation instances
        val x = location.globalX
        val y = location.globalY
        val z = location.globalZ
        val result = shouldRenderSideAgainst(world, location.add(0, 1, 0)) ||
                shouldRenderSideAgainst(world, location.add(0, -2, 0)) ||
                shouldRenderSideAgainst(world, location.add(0, 1, 1)) ||
                shouldRenderSideAgainst(world, location.add(0, 0, -2)) ||
                shouldRenderSideAgainst(world, location.add(1, 0, 1)) ||
                shouldRenderSideAgainst(world, location.add(-2, 0, 0))
        location.set(x, y, z)
        return result
    }

    override fun getMaxVertexCount(): Int
    {
        return 6 * 4 * 4
    }

    override fun bake(world: World, location: BlockLocationMutable, vertexBuffer: FloatBuffer)
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
            val tIndex = top.atlasZLayer
            //I don't like how it looks, but it seems to be the most optimized way of doing this.
            //Method put(<float_array>) has individual put(float) calls under the hood anyway.
            vertexBuffer.put(x1).put(y2).put(z1).put(0f).put(0f).put(tIndex)
            vertexBuffer.put(x1).put(y2).put(z2).put(1f).put(0f).put(tIndex)
            vertexBuffer.put(x2).put(y2).put(z2).put(1f).put(1f).put(tIndex)

            vertexBuffer.put(x2).put(y2).put(z2).put(1f).put(1f).put(tIndex)
            vertexBuffer.put(x2).put(y2).put(z1).put(0f).put(1f).put(tIndex)
            vertexBuffer.put(x1).put(y2).put(z1).put(0f).put(0f).put(tIndex)
        }

        if (shouldRenderSideAgainst(world, location.add(0, -2, 0)))
        {
            val tIndex = bottom.atlasZLayer

            vertexBuffer.put(x2).put(y1).put(z2).put(1f).put(1f).put(tIndex)
            vertexBuffer.put(x1).put(y1).put(z2).put(1f).put(0f).put(tIndex)
            vertexBuffer.put(x1).put(y1).put(z1).put(0f).put(0f).put(tIndex)

            vertexBuffer.put(x1).put(y1).put(z1).put(0f).put(0f).put(tIndex)
            vertexBuffer.put(x2).put(y1).put(z1).put(0f).put(1f).put(tIndex)
            vertexBuffer.put(x2).put(y1).put(z2).put(1f).put(1f).put(tIndex)
        }

        if (shouldRenderSideAgainst(world, location.add(0, 1, 1)))
        {
            val tIndex = south.atlasZLayer

            vertexBuffer.put(x2).put(y2).put(z2).put(0f).put(0f).put(tIndex)
            vertexBuffer.put(x1).put(y2).put(z2).put(1f).put(0f).put(tIndex)
            vertexBuffer.put(x1).put(y1).put(z2).put(1f).put(1f).put(tIndex)

            vertexBuffer.put(x1).put(y1).put(z2).put(1f).put(1f).put(tIndex)
            vertexBuffer.put(x2).put(y1).put(z2).put(0f).put(1f).put(tIndex)
            vertexBuffer.put(x2).put(y2).put(z2).put(0f).put(0f).put(tIndex)

        }

        if (shouldRenderSideAgainst(world, location.add(0, 0, -2)))
        {
            val tIndex = north.atlasZLayer

            vertexBuffer.put(x2).put(y1).put(z1).put(0f).put(0f).put(tIndex)
            vertexBuffer.put(x1).put(y1).put(z1).put(1f).put(0f).put(tIndex)
            vertexBuffer.put(x1).put(y2).put(z1).put(1f).put(1f).put(tIndex)

            vertexBuffer.put(x1).put(y2).put(z1).put(1f).put(1f).put(tIndex)
            vertexBuffer.put(x2).put(y2).put(z1).put(0f).put(1f).put(tIndex)
            vertexBuffer.put(x2).put(y1).put(z1).put(0f).put(0f).put(tIndex)
        }

        if (shouldRenderSideAgainst(world, location.add(-1, 0, 1)))
        {
            val tIndex = west.atlasZLayer

            vertexBuffer.put(x1).put(y2).put(z2).put(0f).put(0f).put(tIndex)
            vertexBuffer.put(x1).put(y2).put(z1).put(1f).put(0f).put(tIndex)
            vertexBuffer.put(x1).put(y1).put(z1).put(1f).put(1f).put(tIndex)

            vertexBuffer.put(x1).put(y1).put(z1).put(1f).put(1f).put(tIndex)
            vertexBuffer.put(x1).put(y1).put(z2).put(0f).put(1f).put(tIndex)
            vertexBuffer.put(x1).put(y2).put(z2).put(0f).put(0f).put(tIndex)
        }

        if (shouldRenderSideAgainst(world, location.add(2, 0, 0)))
        {
            val tIndex = east.atlasZLayer

            vertexBuffer.put(x2).put(y2).put(z1).put(0f).put(0f).put(tIndex)
            vertexBuffer.put(x2).put(y2).put(z2).put(1f).put(0f).put(tIndex)
            vertexBuffer.put(x2).put(y1).put(z2).put(1f).put(1f).put(tIndex)

            vertexBuffer.put(x2).put(y1).put(z2).put(1f).put(1f).put(tIndex)
            vertexBuffer.put(x2).put(y1).put(z1).put(0f).put(1f).put(tIndex)
            vertexBuffer.put(x2).put(y2).put(z1).put(0f).put(0f).put(tIndex)
        }
    }
}
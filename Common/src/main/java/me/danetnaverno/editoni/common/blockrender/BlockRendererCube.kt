package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.world.World
import me.danetnaverno.editoni.texture.Texture
import me.danetnaverno.editoni.util.location.BlockLocation
import org.lwjgl.opengl.GL11

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
            top.bind()
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + size, location.globalZ + size)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + size, location.globalZ + size)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + size, location.globalZ + 0.0f)
            GL11.glEnd()
        }

        if (shouldRenderSideAgainst(world, location.add(0, -1, 0)))
        {
            bottom.bind()
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + 0.0f, location.globalZ + size)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size)
            GL11.glEnd()
        }

        if (shouldRenderSideAgainst(world, location.add(0, 0, 1)))
        {
            south.bind()
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + size, location.globalZ + size)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + size, location.globalZ + size)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + 0.0f, location.globalZ + size)
            GL11.glEnd()
        }

        if (shouldRenderSideAgainst(world, location.add(0, 0, -1)))
        {
            north.bind()
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + size, location.globalZ + 0.0f)
            GL11.glEnd()
        }

        if (shouldRenderSideAgainst(world, location.add(-1, 0, 0)))
        {
            west.bind()
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + size, location.globalZ + size)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + size, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + 0.0f, location.globalY + 0.0f, location.globalZ + size)
            GL11.glEnd()
        }

        if (shouldRenderSideAgainst(world, location.add(1, 0, 0)))
        {
            east.bind()
            GL11.glBegin(GL11.GL_QUADS)
            GL11.glTexCoord2f(0.0f, 0.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + size, location.globalZ + 0.0f)
            GL11.glTexCoord2f(1.0f, 0.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + size, location.globalZ + size)
            GL11.glTexCoord2f(1.0f, 1.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + 0.0f, location.globalZ + size)
            GL11.glTexCoord2f(0.0f, 1.0f)
            GL11.glVertex3f(location.globalX + size, location.globalY + 0.0f, location.globalZ + 0.0f)
            GL11.glEnd()
        }
    }
}
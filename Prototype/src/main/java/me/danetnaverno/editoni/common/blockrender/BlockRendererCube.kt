package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.engine.texture.Texture
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

    override fun draw(block: Block)
    {
        val size = getSize()

        top.bind()
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
        bottom.bind()
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
        south.bind()
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
        north.bind()
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
        west.bind()
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
        east.bind()
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
}

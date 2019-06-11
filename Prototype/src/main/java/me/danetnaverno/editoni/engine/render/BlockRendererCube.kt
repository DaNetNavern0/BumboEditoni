package me.danetnaverno.editoni.engine.render

import me.danetnaverno.editoni.engine.texture.Texture
import org.lwjgl.opengl.GL11

class BlockRendererCube : BlockRenderer
{
    private val top : Texture
    private val bottom : Texture
    private val north : Texture
    private val west : Texture
    private val south : Texture
    private val east : Texture

    constructor(texture: Texture)
    {
        this.top = texture
        this.bottom = texture
        this.north = texture
        this.west = texture
        this.south = texture
        this.east = texture
    }

    constructor(top: Texture, bottom: Texture, side : Texture)
    {
        this.top = top
        this.bottom = bottom
        this.north = side
        this.west = side
        this.south = side
        this.east = side
    }

    constructor(top: Texture, bottom: Texture, north : Texture, west : Texture, south : Texture, east : Texture)
    {
        this.top = top
        this.bottom = bottom
        this.north = north
        this.west = west
        this.south = south
        this.east = east
    }

    override fun draw()
    {
        top.bind()
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(0.0f, 1.0f, 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(0.0f, 1.0f, 1.0f)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(1.0f, 1.0f, 1.0f)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(1.0f, 1.0f, 0.0f)
        GL11.glEnd()
        bottom.bind()
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(0.0f, 0.0f, 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(1.0f, 0.0f, 0.0f)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(1.0f, 0.0f, 1.0f)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(0.0f, 0.0f, 1.0f)
        GL11.glEnd()
        south.bind()
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(1.0f, 1.0f, 1.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(0.0f, 1.0f, 1.0f)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(0.0f, 0.0f, 1.0f)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(1.0f, 0.0f, 1.0f)
        GL11.glEnd()
        north.bind()
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(1.0f, 0.0f, 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(0.0f, 0.0f, 0.0f)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(0.0f, 1.0f, 0.0f)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(1.0f, 1.0f, 0.0f)
        GL11.glEnd()
        west.bind()
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(0.0f, 1.0f, 1.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(0.0f, 1.0f, 0.0f)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(0.0f, 0.0f, 0.0f)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(0.0f, 0.0f, 1.0f)
        GL11.glEnd()
        east.bind()
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0.0f, 0.0f)
        GL11.glVertex3f(1.0f, 1.0f, 0.0f)
        GL11.glTexCoord2f(1.0f, 0.0f)
        GL11.glVertex3f(1.0f, 1.0f, 1.0f)
        GL11.glTexCoord2f(1.0f, 1.0f)
        GL11.glVertex3f(1.0f, 0.0f, 1.0f)
        GL11.glTexCoord2f(0.0f, 1.0f)
        GL11.glVertex3f(1.0f, 0.0f, 0.0f)
        GL11.glEnd()
    }
}

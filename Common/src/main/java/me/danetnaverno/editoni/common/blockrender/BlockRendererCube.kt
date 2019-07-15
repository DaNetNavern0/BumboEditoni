package me.danetnaverno.editoni.common.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.common.ResourceLocation
import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.texture.Texture
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

    override fun draw(block: Block)
    {
        val size = getSize()

        GL11.glPushMatrix()
        GL11.glTranslatef(block.location.globalX.toFloat(), block.location.globalY.toFloat(), block.location.globalZ.toFloat())

        if (block.location.localY == 255 || shouldRenderSideAgainst(block.chunk.getBlockAt(block.location.add(0, 1, 0))))
        {
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
        }
        GL11.glPopMatrix()
        if (true)
            return
        if (block.location.localY == 0 || shouldRenderSideAgainst(block.chunk.getBlockAt(block.location.add(0, -1, 0))))
        {
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
        }

        var nextCoords = block.location.add(0, 0, 1)
        var nextChunk = block.chunk.world.getChunkIfLoaded(nextCoords.toChunkLocation())
        if (nextChunk!=null && nextChunk.isLoaded && shouldRenderSideAgainst(nextChunk.getBlockAt(nextCoords)))
        {
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
        }
        nextCoords = block.location.add(0, 0, -1)
        nextChunk = block.chunk.world.getChunkIfLoaded(nextCoords.toChunkLocation())
        if (nextChunk!=null && nextChunk.isLoaded && shouldRenderSideAgainst(nextChunk.getBlockAt(nextCoords)))
        {
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
        }
        nextCoords = block.location.add(-1, 0, 0)
        nextChunk = block.chunk.world.getChunkIfLoaded(nextCoords.toChunkLocation())
        if (nextChunk!=null && nextChunk.isLoaded && shouldRenderSideAgainst(nextChunk.getBlockAt(nextCoords)))
        {
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
        }
        nextCoords = block.location.add(1, 0, 0)
        nextChunk = block.chunk.world.getChunkIfLoaded(nextCoords.toChunkLocation())
        if (nextChunk!=null && nextChunk.isLoaded && shouldRenderSideAgainst(nextChunk.getBlockAt(nextCoords)))
        {
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
        GL11.glPopMatrix()
    }
}
package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.io.MCAExtraInfo
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import me.danetnaverno.editoni.util.location.toChunkBlockIndex
import me.danetnaverno.editoni.util.location.toSectionBlockIndex
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*
import java.nio.FloatBuffer

class Chunk(@JvmField val world: World, @JvmField val location: ChunkLocation, val extras: MCAExtraInfo, private val entities: Collection<Entity>)
{
    private lateinit var textureCoordData: FloatBuffer
    lateinit var blockTypes: Array<Array<BlockType?>?>
    lateinit var blockStates: MutableMap<Int, BlockState>
    lateinit var tileEntities: MutableMap<Int, TileEntity>

    var vboVertexes: Int = 0
    var vboTexCoords: Int = 0
    var vertexCount = 0

    fun load(blockTypes: Array<Array<BlockType?>?>, blockStates: MutableMap<Int, BlockState>, tileEntities: MutableMap<Int, TileEntity>)
    {
        this.blockTypes = blockTypes
        this.blockStates = blockStates
        this.tileEntities = tileEntities
    }

    fun getBlockAt(location: BlockLocation): Block?
    {
        if (!location.isValid())
            return null
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        val index = (location.localY % 16) * 256 + location.localZ * 16 + location.localX
        val section = location.localY / 16
        val array = blockTypes[section]
        if (array == null || array[index] == null)
            return null
        return Block(this, location.immutable(), array[index]!!, getBlockStateAt(location), getTileEntityAt(location))
    }

    fun getBlockTypeAt(location: BlockLocation): BlockType?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        val section = blockTypes[location.localY / 16] ?: return null
        return section[location.toSectionBlockIndex()]
    }

    fun getBlockStateAt(location: BlockLocation): BlockState?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        return blockStates[location.toChunkBlockIndex()]
    }


    fun getTileEntityAt(location: BlockLocation): TileEntity?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        return tileEntities[location.toChunkBlockIndex()]
    }

    fun setBlock(block: Block)
    {
        val sectionId = block.location.localY / 16
        val cindex = block.location.toChunkBlockIndex()
        var section = blockTypes[sectionId]
        if (section == null)
        {
            section = arrayOfNulls(4096)
            blockTypes[sectionId] = section
        }
        section[block.location.toSectionBlockIndex()] = block.type
        if (block.state != null)
            blockStates[cindex] = block.state
        if (block.tileEntity != null)
            tileEntities[cindex] = block.tileEntity
    }

    fun getEntities(): Collection<Entity>
    {
        return entities.toList()
    }

    fun invalidateVertexes()
    {
        glDeleteBuffers(vboVertexes)
        glDeleteBuffers(vboTexCoords)
        vboVertexes = 0
        vboTexCoords = 0
        vertexCount = 0
    }

    fun updateVertexes()
    {
        glDeleteBuffers(vboVertexes)
        glDeleteBuffers(vboTexCoords)

        val mutableLocation = BlockLocation.Mutable(0, 0, 0)
        val vertexes = arrayListOf<Float>()
        val texCoords = arrayListOf<Float>()

        for (section in 0..15)
        {
            val blockTypes = blockTypes[section] ?: continue
            for (index in 0..4095)
            {
                val blockType = blockTypes[index] ?: continue
                mutableLocation.blockLocationFromSectionIndex(this, section, index)
                if (blockType.renderer.isVisible(world, mutableLocation))
                    blockType.renderer.draw(world, mutableLocation, vertexes, texCoords)
            }
        }

        val vertexData = BufferUtils.createFloatBuffer(vertexes.size)
        textureCoordData = BufferUtils.createFloatBuffer(texCoords.size)
        for (vertex in vertexes)
            vertexData.put(vertex)
        for (color in texCoords)
            textureCoordData.put(color)
        vertexData.flip()
        textureCoordData.flip()
        vertexCount = vertexes.size

        vboVertexes = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexes)
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)

        vboTexCoords = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboTexCoords)
        glBufferData(GL_ARRAY_BUFFER, textureCoordData, GL_STATIC_DRAW)
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, textureCoordData)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun draw()
    {
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexes)
        glVertexPointer(3, GL_FLOAT, 0, 0)

        glBindBuffer(GL_ARRAY_BUFFER, vboTexCoords)
        glTexCoordPointer(3, GL_FLOAT, 0, 0)

        glEnableClientState(GL_VERTEX_ARRAY)
        glEnableClientState(GL_TEXTURE_COORD_ARRAY)

        glDrawArrays(GL_QUADS, 0, vertexCount)
        glDisableClientState(GL_VERTEX_ARRAY)
        glDisableClientState(GL_TEXTURE_COORD_ARRAY)
    }
}
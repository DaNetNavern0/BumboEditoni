package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.io.MCAExtraInfo
import me.danetnaverno.editoni.location.BlockLocation
import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.location.toChunkBlockIndex
import me.danetnaverno.editoni.location.toSectionBlockIndex
import org.lwjgl.opengl.GL44.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

class Chunk(@JvmField val world: World, @JvmField val location: ChunkLocation, val extras: MCAExtraInfo, private val entities: Collection<Entity>)
{
    lateinit var blockTypes: Array<Array<BlockType?>?>
    lateinit var blockStates: MutableMap<Int, BlockState>
    lateinit var tileEntities: MutableMap<Int, TileEntity>

    val vertexData = ChunkVertexData(this)

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

    fun draw()
    {
        vertexData.draw()
    }

    class ChunkVertexData(private val chunk: Chunk)
    {
        val isBuilt: Boolean
            get() = vertexCount != -1

        private var vboVertexes: Int = -1
        private var vboUV: Int = -1
        private var vertexCount: Int = -1

        fun invalidate()
        {
            glDeleteBuffers(vboVertexes)
            glDeleteBuffers(vboUV)
            vertexCount = -1
        }

        fun updateVertexes()
        {
            glDeleteBuffers(vboVertexes)
            glDeleteBuffers(vboUV)
            var vertexBuffer = MemoryUtil.memAllocFloat(32768)
            var uvBuffer = MemoryUtil.memAllocFloat(32768)
            val mutableLocation = BlockLocation.Mutable(0, 0, 0)

            for (section in 0..15)
            {
                val blockTypes = chunk.blockTypes[section] ?: continue
                for (index in 0..4095)
                {
                    val blockType = blockTypes[index] ?: continue
                    mutableLocation.blockLocationFromSectionIndex(chunk, section, index)
                    if (blockType.renderer.isVisible(chunk.world, mutableLocation))
                        blockType.renderer.draw(chunk.world, mutableLocation, vertexBuffer, uvBuffer)

                    if (vertexBuffer.position() >= vertexBuffer.capacity() - 100)
                    {
                        val pair = growBuffers(vertexBuffer, uvBuffer)
                        vertexBuffer = pair.first
                        uvBuffer = pair.second
                    }
                }
            }

            vertexCount = vertexBuffer.position()

            vertexBuffer.flip()
            uvBuffer.flip()

            vboVertexes = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vboVertexes)
            glBufferStorage(GL_ARRAY_BUFFER, vertexBuffer, 0)

            vboUV = glGenBuffers()
            glBindBuffer(GL_ARRAY_BUFFER, vboUV)
            glBufferStorage(GL_ARRAY_BUFFER, uvBuffer, 0)

            MemoryUtil.memFree(vertexBuffer)
            MemoryUtil.memFree(uvBuffer)
        }

        private fun growBuffers(vertexBuffer: FloatBuffer, uvBuffer: FloatBuffer): Pair<FloatBuffer, FloatBuffer>
        {
            val newCapacity = vertexBuffer.capacity() + 32768
            val newVertexBuffer = MemoryUtil.memAllocFloat(newCapacity)
            MemoryUtil.memCopy(vertexBuffer, newVertexBuffer)
            val newUvBuffer = MemoryUtil.memAllocFloat(newCapacity)
            MemoryUtil.memCopy(uvBuffer, newUvBuffer)

            MemoryUtil.memFree(vertexBuffer)
            MemoryUtil.memFree(uvBuffer)
            return Pair(newVertexBuffer, newUvBuffer)
        }

        fun draw()
        {
            if (vertexCount <= 0)
                return

            glBindBuffer(GL_ARRAY_BUFFER, vboVertexes)
            glVertexPointer(3, GL_FLOAT, 0, 0)

            glBindBuffer(GL_ARRAY_BUFFER, vboUV)
            glTexCoordPointer(3, GL_FLOAT, 0, 0)

            glEnableClientState(GL_VERTEX_ARRAY)
            glEnableClientState(GL_TEXTURE_COORD_ARRAY)

            glDrawArrays(GL_QUADS, 0, vertexCount)
            glDisableClientState(GL_VERTEX_ARRAY)
            glDisableClientState(GL_TEXTURE_COORD_ARRAY)
        }
    }
}
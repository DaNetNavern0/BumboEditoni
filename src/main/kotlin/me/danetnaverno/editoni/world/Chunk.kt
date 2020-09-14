package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.io.MCAExtraInfo
import me.danetnaverno.editoni.location.*
import me.danetnaverno.editoni.render.ChunkRenderer

class Chunk(@JvmField val world: World, @JvmField val location: ChunkLocation, val extras: MCAExtraInfo, private val entities: Collection<Entity>)
{
    /*private*/ lateinit var blockTypes: Array<Array<BlockType?>?>
    /*private*/ lateinit var blockStates: MutableMap<Int, BlockState>
    /*private*/ lateinit var tileEntities: MutableMap<Int, TileEntity>

    val vertexData = ChunkRenderer(this)

    val region
        get() = world.getRegion(location.toRegionLocation())!!

    fun load(blockTypes: Array<Array<BlockType?>?>, blockStates: MutableMap<Int, BlockState>, tileEntities: MutableMap<Int, TileEntity>)
    {
        this.blockTypes = blockTypes
        this.blockStates = blockStates
        this.tileEntities = tileEntities
    }

    fun getBlockAt(location: IBlockLocation): Block?
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


        return Block(this, location.toImmutable(), array[index]!!, getBlockStateAt(location), getTileEntityAt(location))
    }

    fun getBlockTypeAt(location: IBlockLocation): BlockType?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        val section = blockTypes[location.localY / 16] ?: return null
        return section[location.toSectionBlockIndex()]
    }

    fun getBlockStateAt(location: IBlockLocation): BlockState?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        return blockStates[location.toChunkBlockIndex()]
    }


    fun getTileEntityAt(location: IBlockLocation): TileEntity?
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

    object Placeholder
}
package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.blockstate.BlockState
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.io.MCAExtraInfo
import me.danetnaverno.editoni.location.ChunkLocation
import me.danetnaverno.editoni.location.IBlockLocation
import me.danetnaverno.editoni.render.ChunkRenderer

class Chunk(val world: World, val chunkLocation: ChunkLocation, val extras: MCAExtraInfo, private val entities: Collection<Entity>)
{
    //todo using Minecraft's palette approach would be nice and save plenty of memory
    // especially when it comes to Block States (things like Observer rotation, crop's groth stages etc, don't confuse with TileEntities)
    // also, making these fields private would be nice, but again, WIP
    /*private*/ lateinit var blockTypes: Array<Array<BlockType?>?>
    /*private*/ lateinit var blockStates: MutableMap<Int, BlockState>
    /*private*/ lateinit var tileEntities: MutableMap<Int, TileEntity>

    val renderer = ChunkRenderer(this)

    val region
        get() = world.getRegion(chunkLocation.toRegionLocation())!!

    fun load(blockTypes: Array<Array<BlockType?>?>, blockStates: MutableMap<Int, BlockState>, tileEntities: MutableMap<Int, TileEntity>)
    {
        this.blockTypes = blockTypes
        this.blockStates = blockStates
        this.tileEntities = tileEntities
    }

    fun getBlockAt(blockLocation: IBlockLocation): Block?
    {
        if (!blockLocation.isValid())
            return null
        require(this.chunkLocation.isBlockLocationBelongs(blockLocation)) {
            "Location is out of chunk boundaries: chunkLocation=${this.chunkLocation} blockLocation=$blockLocation"
        }
        val index = (blockLocation.localY % 16) * 256 + blockLocation.localZ * 16 + blockLocation.localX
        val section = blockLocation.localY / 16
        val array = blockTypes[section]
        if (array == null || array[index] == null)
            return null


        return Block(this, blockLocation.toImmutable(), array[index]!!, getBlockStateAt(blockLocation), getTileEntityAt(blockLocation))
    }

    fun getBlockTypeAt(blockLocation: IBlockLocation): BlockType?
    {
        require(this.chunkLocation.isBlockLocationBelongs(blockLocation)) {
            "Location is out of chunk boundaries: chunkLocation=${this.chunkLocation}; blockLocation=$blockLocation"
        }
        val section = blockTypes[blockLocation.localY / 16] ?: return null
        return section[blockLocation.toSectionBlockIndex()]
    }

    fun getBlockStateAt(blockLocation: IBlockLocation): BlockState?
    {
        require(this.chunkLocation.isBlockLocationBelongs(blockLocation)) {
            "Location is out of chunk boundaries: chunkLocation=${this.chunkLocation}; blockLocation=$blockLocation"
        }
        return blockStates[blockLocation.toChunkBlockIndex()]
    }


    fun getTileEntityAt(blockLocation: IBlockLocation): TileEntity?
    {
        require(this.chunkLocation.isBlockLocationBelongs(blockLocation)) {
            "Location is out of chunk boundaries: chunkLocation=${this.chunkLocation} blockLocation=$blockLocation"
        }
        return tileEntities[blockLocation.toChunkBlockIndex()]
    }

    fun setBlock(block: Block)
    {
        val sectionId = block.blockLocation.localY / 16
        val cindex = block.blockLocation.toChunkBlockIndex()
        var section = blockTypes[sectionId]
        if (section == null)
        {
            section = arrayOfNulls(4096)
            blockTypes[sectionId] = section
        }
        section[block.blockLocation.toSectionBlockIndex()] = block.type
        if (block.state != null)
            blockStates[cindex] = block.state
        if (block.tileEntity != null)
            tileEntities[cindex] = block.tileEntity
    }

    fun getEntities(): Collection<Entity>
    {
        return entities.toList()
    }

    object Placeholder
}
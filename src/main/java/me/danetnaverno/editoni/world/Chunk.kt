package me.danetnaverno.editoni.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.io.MCAExtraInfo
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import me.danetnaverno.editoni.util.location.toChunkBlockIndex
import me.danetnaverno.editoni.util.location.toSectionBlockIndex
import net.querz.nbt.tag.CompoundTag

class Chunk(@JvmField val world: World, @JvmField val location: ChunkLocation, val extras: MCAExtraInfo, private val entities: Collection<Entity>)
{
    lateinit var blockTypes: Array<Array<BlockType?>?>
    lateinit var blockStates: MutableMap<Int, BlockState>
    lateinit var tileEntities: MutableMap<Int, TileEntity>

    fun load(blockTypes: Array<Array<BlockType?>?>, blockStates: MutableMap<Int, BlockState>, tileEntities: MutableMap<Int, TileEntity>)
    {
        this.blockTypes = blockTypes
        this.blockStates = blockStates
        this.tileEntities = tileEntities
    }

    fun getBlockAt(location: BlockLocation): Block?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        val index = (location.localY % 16) * 256 + location.localZ * 16 + location.localX
        val section = location.localY / 16
        val array = blockTypes[section]
        if (array == null || array[index] == null)
            return null
        return Block(this, location, array[index]!!, getBlockStateAt(location), getTileEntityAt(location))
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
}
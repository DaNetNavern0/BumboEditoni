package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.common.world.*
import me.danetnaverno.editoni.minecraft.util.location.toChunkBlockIndex
import me.danetnaverno.editoni.minecraft.util.location.toSectionBlockIndex
import me.danetnaverno.editoni.minecraft.world.io.MCAExtraInfo
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation

class MinecraftChunk(world: MinecraftWorld, location: ChunkLocation, val extras: MCAExtraInfo, private val entities: Collection<Entity>)
    : Chunk(world, location)
{
    private lateinit var blockTypes : Array<Array<BlockType?>?>
    private lateinit var blockStates : MutableMap<Int, BlockState?>
    private lateinit var tileEntities : MutableMap<Int, TileEntity?>

    fun load(blockTypes: Array<Array<BlockType?>?>, blockStates: MutableMap<Int, BlockState?>, tileEntities: MutableMap<Int, TileEntity?>)
    {
        this.blockTypes = blockTypes
        this.blockStates = blockStates
        this.tileEntities = tileEntities
    }

    override fun getEntities(): Collection<Entity>
    {
        return entities.toList()
    }

    override fun getBlockTypes(): Array<Array<BlockType?>?>
    {
        return blockTypes.clone()
    }

    fun getBlockStates(): Map<Int, BlockState?>
    {
        return blockStates.toMap()
    }

    fun getTileEntities(): Map<Int, TileEntity?>
    {
        return tileEntities.toMap()
    }

    override fun getBlockAt(location: BlockLocation): Block?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        val index = (location.localY % 16) * 256 + location.localZ * 16 + location.localX
        val section = location.localY / 16
        val array = blockTypes[section]
        if (array==null || array[index]==null)
            return null
        return MinecraftBlock(this, location, array[index]!!, getBlockStateAt(location), getTileEntityAt(location))
    }

    override fun getBlockTypeAt(location: BlockLocation): BlockType?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        val section = blockTypes[location.localY / 16] ?: return null
        return section[location.toSectionBlockIndex()]
    }

    override fun getBlockStateAt(location: BlockLocation): BlockState?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        return blockStates[location.toChunkBlockIndex()]
    }


    override fun getTileEntityAt(location: BlockLocation): TileEntity?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        return tileEntities[location.toChunkBlockIndex()]
    }

    override fun setBlock(block: Block)
    {
        val section = block.location.localY / 16
        val cindex = block.location.toChunkBlockIndex()
        blockTypes[section]!![block.location.toSectionBlockIndex()] = block.type
        if (block.state != null)
            blockStates[cindex] = block.state
        if (block.tileEntity != null)
            tileEntities[cindex] = block.tileEntity
    }
}
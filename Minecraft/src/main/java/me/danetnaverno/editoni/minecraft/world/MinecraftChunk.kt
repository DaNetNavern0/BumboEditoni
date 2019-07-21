package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.common.world.*
import me.danetnaverno.editoni.minecraft.util.location.toChunkBlockIndex
import me.danetnaverno.editoni.minecraft.util.location.toSectionBlockIndex
import me.danetnaverno.editoni.minecraft.world.io.MCAExtraInfo
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import java.util.*

class MinecraftChunk(world: MinecraftWorld, location: ChunkLocation, renderX: Int, renderZ: Int, val extras: MCAExtraInfo, private val entities: Collection<Entity>)
    : Chunk(world, location, renderX, renderZ)
{
    private lateinit var blockTypes : Array<Array<BlockType?>?>
    private lateinit var blockStates : Map<Int, BlockState>
    private lateinit var tileEntities : Map<Int, TileEntity>

    fun load(blockTypes: Array<Array<BlockType?>?>, blockStates: Map<Int, BlockState>, tileEntities: Map<Int, TileEntity>)
    {
        this.blockTypes = blockTypes
        this.blockStates = blockStates
        this.tileEntities = tileEntities
    }

    override fun getEntities(): Collection<Entity>
    {
        return ArrayList(entities)
    }

    override fun getBlockTypes(): Array<Array<BlockType?>?>
    {
        return blockTypes.clone()
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
        return MinecraftBlock(this, location, array[index]!!)
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
        val index = (block.location.localY % 16) * 256 + block.location.localZ * 16 + block.location.localX
        val section = block.location.localY / 16
        //blocks[section]!![index] = block
    }
}
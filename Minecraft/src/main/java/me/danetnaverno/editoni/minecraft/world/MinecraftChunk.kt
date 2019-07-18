package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.world.*
import me.danetnaverno.editoni.minecraft.world.io.MCAExtraInfo
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import java.util.*

class MinecraftChunk(world: MinecraftWorld, location: ChunkLocation, renderX: Int, renderZ: Int,
                     val extras: MCAExtraInfo,
                     private val blockStates : Array<Array<BlockState?>?>,
                     private val tileEntities : Map<Int, TileEntity>,
                     private val entities: Collection<Entity>)
    : Chunk(world, location, renderX, renderZ)
{
    override fun getEntities(): Collection<Entity>
    {
        return ArrayList(entities)
    }

    fun getBlockStateAt(location: BlockLocation): BlockState?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        val index = (location.localY % 16) * 256 + location.localZ * 16 + location.localX
        val sind = location.localY / 16
        val section = blockStates[sind] ?: return null
        return section[index]
    }

    fun getBlockStateAt(x: Int, y: Int, z: Int): BlockState?
    {
        val section = blockStates[y / 16] ?: return null
        return section[(y % 16) * 256 + z * 16 + x]
    }

    override fun getBlockAt(location: BlockLocation): Block?
    {
        val state = getBlockStateAt(location) ?: return null
        return MinecraftBlock(this, location, state.getType(), getBlockStateAt(location), null)
    }

    override fun setBlock(block: Block)
    {
        val index = (block.location.localY % 16) * 256 + block.location.localZ * 16 + block.location.localX
        val section = block.location.localY / 16
        blockStates[section]!![index] = block.state
    }
}

package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.minecraft.world.io.MCAExtraInfo
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import java.util.*

class MinecraftChunk(world: MinecraftWorld, location: ChunkLocation, renderX: Int, renderZ: Int, val extras: MCAExtraInfo, private val entities: Collection<Entity>)
    : Chunk(world, location, renderX, renderZ)
{
    private val blocks = Array<Array<Block?>?>(16) { null }

    fun load(blocks: Collection<Block>)
    {
        for (block in blocks)
        {
            val index = (block.location.localY % 16) * 256 + block.location.localZ * 16 + block.location.localX
            val section = block.location.localY / 16
            if (this.blocks[section] == null)
                this.blocks[section] = Array(4096) { null }
            this.blocks[section]!![index] = block
        }
    }

    override fun getEntities(): Collection<Entity>
    {
        return ArrayList(entities)
    }

    override fun getBlocks(): Iterable<Block>
    {
        return blocks.flatMap { it?.filterNotNull() ?: listOf() }
    }

    override fun getBlockAt(location: BlockLocation): Block?
    {
        require(this.location.isBlockLocationBelongs(location)) {
            "Position is out of chunk boundaries: chunkLocation=${this.location} location=$location"
        }
        val index = (location.localY % 16) * 256 + location.localZ * 16 + location.localX
        val section = location.localY / 16
        val array = blocks[section] ?: return null
        return array[index]
    }

    override fun setBlock(block: Block)
    {
        val index = (block.location.localY % 16) * 256 + block.location.localZ * 16 + block.location.localX
        val section = block.location.localY / 16
        blocks[section]!![index] = block
    }
}
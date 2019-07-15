package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.minecraft.world.io.MCAExtraInfo
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import java.util.*

class MinecraftChunk(world: MinecraftWorld, location: ChunkLocation, renderX: Int, renderZ: Int, val extras: MCAExtraInfo,
                     private val blocks: MutableMap<BlockLocation, Block>, private val entities: Collection<Entity>)
    : Chunk(world, location, renderX, renderZ)
{
    override fun getEntities(): Collection<Entity>
    {
        check(isLoaded) { "Chunk is still loading: $this" }
        return ArrayList(entities)
    }

    override fun getBlocks(): Collection<Block>
    {
        check(isLoaded) { "Chunk is still loading: $this" }
        return ArrayList(blocks.values)
    }

    override fun getBlockAt(blockPos: BlockLocation): Block
    {
        check(isLoaded) { "Chunk is still loading: $this" }
        require(location.isBlockLocationBelongs(blockPos)) {
            "Position is out of chunk boundaries: chunkLocation=$location blockPos=$blockPos"
        }
        return blocks[blockPos]!!
    }

    override fun setBlock(block: Block)
    {
        check(isLoaded) { "Chunk is still loading: $this" }
        blocks[block.location] = block
    }

    override fun isLoaded(): Boolean
    {
        return !blocks.isEmpty()
    }
}
package me.danetnaverno.editoni.minecraft.world

import me.danetnaverno.editoni.common.world.Block
import me.danetnaverno.editoni.common.world.Chunk
import me.danetnaverno.editoni.common.world.Entity
import me.danetnaverno.editoni.minecraft.world.io.MCAExtraInfo
import org.joml.Vector3i
import java.util.*

class MinecraftChunk(val extras: MCAExtraInfo, xRender: Int, zRender: Int, xPos: Int, zPos: Int,
                     private val blocks: MutableMap<Vector3i, Block>, private val entities: Collection<Entity>)
    : Chunk(xRender, zRender, xPos, zPos)
{

    override fun getBlocks(): Collection<Block>
    {
        check(isLoaded) { "Chunk is still loading: $this" }
        return ArrayList(blocks.values)
    }

    override fun getBlockAt(pos: Vector3i): Block
    {
        check(isLoaded) { "Chunk is still loading: $this" }
        require(pos.x in 0..15 && pos.z in 0..15) {
            "Position is out of chunk boundaries: chunk=$this; pos=$pos"
        }
        return blocks[pos]!!
    }

    override fun setBlock(block: Block)
    {
        check(isLoaded) { "Chunk is still loading: $this" }
        blocks[block.localPos] = block
    }

    override fun getEntities(): Collection<Entity>
    {
        check(isLoaded) { "Chunk is still loading: $this" }
        return ArrayList(entities)
    }
}
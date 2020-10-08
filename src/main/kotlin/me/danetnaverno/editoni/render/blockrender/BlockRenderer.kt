package me.danetnaverno.editoni.render.blockrender

import com.alibaba.fastjson.JSONObject
import me.danetnaverno.editoni.location.BlockLocationMutable
import me.danetnaverno.editoni.world.World
import java.nio.FloatBuffer

abstract class BlockRenderer
{
    abstract fun fromJson(data: JSONObject)
    abstract fun isVisible(world: World, blockLocation: BlockLocationMutable): Boolean
    abstract fun getMaxVertexCount() : Int
    abstract fun bake(world: World, blockLocation: BlockLocationMutable, vertexBuffer: FloatBuffer)

    open fun shouldRenderSideAgainst(world: World, blockLocation: BlockLocationMutable): Boolean
    {
        if (blockLocation.localY < 0 || blockLocation.localY > 255)
            return true

        //todo These 2 lines result in full hierarchy walk from the world to a specific neighboring block,
        //   and it's called 6 times per each block in a chunk that gets baked at this moment.
        // This could definitely use some optimization, but it doesn't make the user experience horrible for now,
        //   because it happens only when we bake a chunk, which happens only once when we load or alter a chunk.
        val chunk = world.getChunk(blockLocation) ?: return false
        val type = chunk.getBlockTypeAt(blockLocation) ?: return true
        return !type.isOpaque
    }
}

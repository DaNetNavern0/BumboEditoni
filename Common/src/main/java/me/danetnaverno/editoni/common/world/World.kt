package me.danetnaverno.editoni.common.world

import me.danetnaverno.editoni.common.blocktype.BlockType
import me.danetnaverno.editoni.common.world.io.IWorldIOProvider
import me.danetnaverno.editoni.util.location.BlockLocation
import me.danetnaverno.editoni.util.location.ChunkLocation
import me.danetnaverno.editoni.util.location.EntityLocation
import java.nio.file.Path

abstract class World
{
    lateinit var worldRenderer: WorldRenderer
    lateinit var worldIOProvider: IWorldIOProvider
    abstract var path: Path
        protected set

    abstract val loadedChunks: List<Chunk>

    abstract fun getChunkIfLoaded(location: ChunkLocation): Chunk?
    abstract fun getChunk(location: ChunkLocation): Chunk?
    abstract fun createChunk(location: ChunkLocation): Chunk
    abstract fun loadChunkAt(chunkLocation: ChunkLocation)
    abstract fun unloadChunks(chunksToUnload: List<Chunk>)

    abstract fun getBlockAt(location: BlockLocation): Block?
    abstract fun getBlockTypeAt(location: BlockLocation): BlockType?
    abstract fun getBlockStateAt(location: BlockLocation): BlockState?
    abstract fun getTileEntityAt(location: BlockLocation): TileEntity?
    abstract fun getLoadedBlockAt(location: BlockLocation): Block?
    abstract fun getLoadedBlockTypeAt(location: BlockLocation): BlockType?
    abstract fun getLoadedBlockStateAt(location: BlockLocation): BlockState?
    abstract fun getLoadedTileEntityAt(location: BlockLocation): TileEntity?

    abstract fun setBlock(block: Block)
    abstract fun deleteBlock(location: BlockLocation)

    abstract fun getEntitiesAt(location: EntityLocation, radius: Float): List<Entity>
}

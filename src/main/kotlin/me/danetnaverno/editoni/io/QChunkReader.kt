package me.danetnaverno.editoni.io

import kotlinx.coroutines.withContext
import me.danetnaverno.editoni.util.BumboChunk
import me.danetnaverno.editoni.util.FileReadingScope
import me.danetnaverno.editoni.util.QuerzChunk
import me.danetnaverno.editoni.world.Region
import me.danetnaverno.editoni.world.World
import net.querz.nbt.tag.CompoundTag
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.nio.file.Path
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaConstructor

abstract class QChunkReader
{
    abstract fun convertFromQChunk(world: World, qChunk: QuerzChunk): BumboChunk?
    abstract fun writeQChunkToFile(saveFolder: Path, region: Region, qChunk: QuerzChunk, globalX: Int, globalZ: Int)
    abstract fun composeQChunk(chunk: BumboChunk): QuerzChunk

    protected abstract fun readQChunkFromFileInner(region: Region, globalX: Int, globalZ: Int): QuerzChunk?

    suspend fun readQChunkFromFile(region: Region, globalX: Int, globalZ: Int): QuerzChunk?
    {
        return withContext(FileReadingScope.coroutineContext) {
            readQChunkFromFileInner(region, globalX, globalZ)
        }
    }

    companion object
    {
        private val dataField: MethodHandle
        private val qChunkConstructor: MethodHandle

        init
        {
            val lookup = MethodHandles.lookup()

            val qChunkConstructorRef = QuerzChunk::class.constructors.first()
            qChunkConstructorRef.isAccessible = true
            qChunkConstructor = lookup.unreflectConstructor(qChunkConstructorRef.javaConstructor)

            val qDataField = QuerzChunk::class.java.getDeclaredField("data")
            qDataField.isAccessible = true
            dataField = lookup.unreflectGetter(qDataField)
        }

        fun getData(chunk: QuerzChunk): CompoundTag
        {
            return dataField.invokeExact(chunk) as CompoundTag
        }

        fun newQChunk(timestamp: Int): QuerzChunk
        {
            return qChunkConstructor.invokeExact(timestamp) as QuerzChunk
        }
    }
}
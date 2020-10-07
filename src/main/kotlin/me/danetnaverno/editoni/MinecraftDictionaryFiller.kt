package me.danetnaverno.editoni

import me.danetnaverno.editoni.blocktype.BlockDictionary
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.render.blockrender.BlockRendererAir
import me.danetnaverno.editoni.render.blockrender.BlockRendererCube
import me.danetnaverno.editoni.render.blockrender.BlockRendererDictionary

/**
 * todo I don't like this, the existence of this class feels hacky.
 */
object MinecraftDictionaryFiller
{
    lateinit var AIR: BlockType

    fun initialize()
    {
        check(!this::AIR.isInitialized) { "MinecraftDictionaryFiller had already been initialized" }

        BlockRendererDictionary.register("minecraft:air", BlockRendererAir::class.java)
        BlockRendererDictionary.register("minecraft:cube", BlockRendererCube::class.java)
        AIR = BlockDictionary.getBlockType("minecraft:air")
    }
}
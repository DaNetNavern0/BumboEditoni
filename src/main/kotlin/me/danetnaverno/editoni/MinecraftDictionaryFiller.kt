package me.danetnaverno.editoni

import me.danetnaverno.editoni.blocktype.BlockDictionary.getBlockType
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.render.blockrender.BlockRendererAir
import me.danetnaverno.editoni.render.blockrender.BlockRendererCube
import me.danetnaverno.editoni.render.blockrender.BlockRendererDictionary.register

/**
 * todo I don't like this, the existence of this class feels hacky.
 */
object MinecraftDictionaryFiller
{
    lateinit var AIR: BlockType

    fun init()
    {
        register("minecraft:air", BlockRendererAir::class.java)
        register("minecraft:cube", BlockRendererCube::class.java)
        AIR = getBlockType("minecraft:air")
    }
}
package me.danetnaverno.editoni

import me.danetnaverno.editoni.blockrender.BlockRendererAir
import me.danetnaverno.editoni.blockrender.BlockRendererCube
import me.danetnaverno.editoni.blockrender.BlockRendererDictionary.register
import me.danetnaverno.editoni.blocktype.BlockDictionary.getBlockType
import me.danetnaverno.editoni.blocktype.BlockType
import me.danetnaverno.editoni.util.ResourceLocation

object MinecraftDictionaryFiller
{
    lateinit var AIR: BlockType

    fun init()
    {
        register(ResourceLocation("minecraft", "air"), BlockRendererAir::class.java)
        register(ResourceLocation("minecraft", "cube"), BlockRendererCube::class.java)
        AIR = getBlockType(ResourceLocation("minecraft:air"))
    }
}